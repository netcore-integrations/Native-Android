package com.netcore.smarttechdemo

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.netcore.android.Smartech
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateProfileScreen : AppCompatActivity() {

    // ── Shared Prefs keys ──────────────────────────────────────────────────
    companion object {
        private const val PREFS         = "profile_prefs"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME  = "last_name"
        private const val KEY_EMAIL      = "email"
        private const val KEY_MOBILE     = "mobile"
        private const val KEY_DOB        = "dob"
        private const val KEY_GENDER     = "gender"

        private val GENDER_OPTIONS = listOf("Select Gender", "Male", "Female", "Other")
    }

    // ── Views ──────────────────────────────────────────────────────────────
    private lateinit var tilFirstName : TextInputLayout
    private lateinit var tilLastName  : TextInputLayout
    private lateinit var tilEmail     : TextInputLayout
    private lateinit var tilMobile    : TextInputLayout
    private lateinit var tilDob       : TextInputLayout
    private lateinit var tilGender    : TextInputLayout

    private lateinit var etFirstName  : TextInputEditText
    private lateinit var etLastName   : TextInputEditText
    private lateinit var etEmail      : TextInputEditText
    private lateinit var etMobile     : TextInputEditText
    private lateinit var etDob        : TextInputEditText
    private lateinit var acvGender    : AutoCompleteTextView

    private lateinit var btnSubmit    : MaterialButton
    private lateinit var btnBack      : ImageView
    private lateinit var btnHeaderSave: ImageView

    // ── Lifecycle ──────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_profile_screen)
        supportActionBar?.hide()

        bindViews()
        setupGenderDropdown()
        restoreFromPrefs()
        setupDatePicker()
        setupValidationWatchers()
        setupButtons()
    }

    // ── Bind views ─────────────────────────────────────────────────────────
    private fun bindViews() {
        tilFirstName  = findViewById(R.id.til_first_name)
        tilLastName   = findViewById(R.id.til_last_name)
        tilEmail      = findViewById(R.id.til_email)
        tilMobile     = findViewById(R.id.til_mobile)
        tilDob        = findViewById(R.id.til_dob)
        tilGender     = findViewById(R.id.til_gender)

        etFirstName   = findViewById(R.id.et_first_name)
        etLastName    = findViewById(R.id.et_last_name)
        etEmail       = findViewById(R.id.et_email)
        etMobile      = findViewById(R.id.et_mobile)
        etDob         = findViewById(R.id.et_dob)
        acvGender     = findViewById(R.id.acv_gender)

        btnSubmit     = findViewById(R.id.btn_submit)
        btnBack       = findViewById(R.id.btn_back)
        btnHeaderSave = findViewById(R.id.btn_header_save)
    }

    // ── Gender exposed dropdown ────────────────────────────────────────────
    private fun setupGenderDropdown() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            GENDER_OPTIONS
        )
        acvGender.setAdapter(adapter)
        acvGender.setOnItemClickListener { _, _, position, _ ->
            acvGender.setText(GENDER_OPTIONS[position], false)
            tilGender.error = null
        }
    }

    // ── Restore saved profile from SharedPrefs ─────────────────────────────
    private fun restoreFromPrefs() {
        val p = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        etFirstName.setText(p.getString(KEY_FIRST_NAME, ""))
        etLastName.setText(p.getString(KEY_LAST_NAME,  ""))
        etEmail.setText(p.getString(KEY_EMAIL,    ""))
        etMobile.setText(p.getString(KEY_MOBILE,  ""))
        etDob.setText(p.getString(KEY_DOB,     ""))
        val saved = p.getString(KEY_GENDER, "") ?: ""
        if (saved.isNotBlank()) acvGender.setText(saved, false)
    }

    // ── Date picker ────────────────────────────────────────────────────────
    private fun setupDatePicker() {
        val openPicker = View.OnClickListener {
            hideKeyboard()
            val cal = Calendar.getInstance()
            // Pre-select existing date if already entered
            val existing = etDob.text?.toString() ?: ""
            if (existing.isNotBlank()) {
                runCatching {
                    cal.time = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(existing)!!
                }
            }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selected = Calendar.getInstance().apply { set(year, month, day) }
                    etDob.setText(
                        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(selected.time)
                    )
                    tilDob.error = null
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()   // no future dates
            }.show()
        }
        etDob.setOnClickListener(openPicker)
        tilDob.setStartIconOnClickListener(openPicker)
    }

    // ── Clear field errors as the user types ───────────────────────────────
    private fun setupValidationWatchers() {
        listOf(
            etFirstName to tilFirstName,
            etLastName  to tilLastName,
            etEmail     to tilEmail,
            etMobile    to tilMobile
        ).forEach { (et, til) ->
            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    til.error = null
                    til.isErrorEnabled = false
                }
            })
        }
    }

    // ── Button wiring ──────────────────────────────────────────────────────
    private fun setupButtons() {
        btnBack.setOnClickListener      { finish() }
        btnHeaderSave.setOnClickListener { submitProfile() }
        btnSubmit.setOnClickListener    { submitProfile() }
    }

    // ── Validation + Smartech/Hansel save ──────────────────────────────────
    private fun submitProfile() {
        hideKeyboard()

        val firstName = etFirstName.text?.toString()?.trim() ?: ""
        val lastName  = etLastName.text?.toString()?.trim()  ?: ""
        val email     = etEmail.text?.toString()?.trim()     ?: ""
        val mobile    = etMobile.text?.toString()?.trim()    ?: ""
        val dob       = etDob.text?.toString()?.trim()       ?: ""
        val gender    = acvGender.text?.toString()
            .takeIf { it != "Select Gender" && !it.isNullOrBlank() } ?: ""

        var valid = true

        if (firstName.isBlank()) {
            tilFirstName.error = "Required"
            tilFirstName.isErrorEnabled = true
            valid = false
        }
        if (lastName.isBlank()) {
            tilLastName.error = "Required"
            tilLastName.isErrorEnabled = true
            valid = false
        }
        if (email.isBlank()) {
            tilEmail.error = "Email is required"
            tilEmail.isErrorEnabled = true
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Enter a valid email address"
            tilEmail.isErrorEnabled = true
            valid = false
        }
        if (mobile.length != 10) {
            tilMobile.error = "Enter a valid 10-digit mobile number"
            tilMobile.isErrorEnabled = true
            valid = false
        }

        if (!valid) return

        // ── Smartech: updateUserProfile ────────────────────────────────
        val payload = hashMapOf<String, Any>(
            "FIRST_NAME" to firstName,
            "LAST_NAME"  to lastName,
            "NAME"       to "$firstName $lastName",
            "EMAIL"      to email,
            "MOBILE"     to mobile
        )
        if (dob.isNotBlank())    payload["DOB"]    = dob
        if (gender.isNotBlank()) payload["GENDER"] = gender

        Smartech.getInstance(WeakReference(applicationContext))
            .updateUserProfile(payload)

        // ── Hansel: putAttribute ───────────────────────────────────────
        with(Hansel.getUser()) {
            putAttribute("FIRST_NAME", firstName)
            putAttribute("LAST_NAME",  lastName)
            putAttribute("NAME",       "$firstName $lastName")
            putAttribute("EMAIL",      email)
            putAttribute("MOBILE",     mobile)
            if (dob.isNotBlank())    putAttribute("DOB",    dob)
            if (gender.isNotBlank()) putAttribute("GENDER", gender)
        }

        // ── Save locally so fields restore on re-open ──────────────────
        getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME,  lastName)
            putString(KEY_EMAIL,      email)
            putString(KEY_MOBILE,     mobile)
            putString(KEY_DOB,        dob)
            putString(KEY_GENDER,     gender)
            apply()
        }

        // ── Track profile update event ─────────────────────────────────
        val eventPayload = hashMapOf<String, Any>(
            "first_name" to firstName,
            "last_name"  to lastName,
            "has_dob"    to dob.isNotBlank(),
            "has_gender" to gender.isNotBlank()
        )
        if (dob.isNotBlank())    eventPayload["dob"]    = dob
        if (gender.isNotBlank()) eventPayload["gender"] = gender
        Smartech.getInstance(WeakReference(applicationContext))
            .trackEvent("profile_updated", eventPayload)

        Snackbar.make(btnSubmit, "✓  Profile saved successfully!", Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.nc_success))
            .setTextColor(getColor(R.color.white))
            .show()
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }
}
