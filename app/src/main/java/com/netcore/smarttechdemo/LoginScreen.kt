package com.netcore.smarttechdemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.netcore.android.Smartech
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference

class LoginScreen : AppCompatActivity() {

    private lateinit var linearBody: LinearLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var btnGuestLogin: TextView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val SHARED_PREF_NAME = "Shared_pref"
        const val KEY_EMAIL        = "Email"
        const val KEY_AUTO_LOGIN   = "auto_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        // ── Auto-login: if email was saved before, skip login screen ──────
        val savedEmail = sharedPreferences.getString(KEY_EMAIL, null)
        val autoLogin  = sharedPreferences.getBoolean(KEY_AUTO_LOGIN, false)
        if (autoLogin && !savedEmail.isNullOrBlank()) {
            goToMain(savedEmail)
            return   // don't inflate the layout at all
        }

        setContentView(R.layout.login_screen)
        supportActionBar?.hide()

        bindViews()
        setupListeners()
    }

    // ── View binding ───────────────────────────────────────────────────────
    private fun bindViews() {
        linearBody    = findViewById(R.id.linearBody)
        tilEmail      = findViewById(R.id.til_email)
        etEmail       = findViewById(R.id.username_field)
        btnLogin      = findViewById(R.id.login_button)
        btnRegister   = findViewById(R.id.register_button)
        btnGuestLogin = findViewById(R.id.btn_guest_login)
    }

    // ── Click listeners ────────────────────────────────────────────────────
    private fun setupListeners() {
        btnLogin.setOnClickListener      { doEmailLogin() }
        btnRegister.setOnClickListener   { startActivity(Intent(this, RegisterScreen::class.java)) }
        btnGuestLogin.setOnClickListener { doGuestLogin() }
    }

    // ── Email login (no password) ──────────────────────────────────────────
    private fun doEmailLogin() {
        val email = etEmail.text?.toString()?.trim() ?: ""

        if (email.isBlank()) {
            tilEmail.error = "Please enter your email"
            tilEmail.isErrorEnabled = true
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Enter a valid email address"
            tilEmail.isErrorEnabled = true
            return
        }
        tilEmail.error = null
        tilEmail.isErrorEnabled = false

        // Save email + enable auto-login for next time
        sharedPreferences.edit()
            .putString(KEY_EMAIL, email)
            .putBoolean(KEY_AUTO_LOGIN, true)
            .apply()

        // Identify user in Smartech and Hansel
        Smartech.getInstance(WeakReference(applicationContext)).login(email)
        Hansel.getUser().setUserId(email)

        goToMain(email)
    }

    // ── Guest login ────────────────────────────────────────────────────────
    private fun doGuestLogin() {
        val guestId = "guest_${System.currentTimeMillis()}"
        // Guest sessions do NOT enable auto-login
        Smartech.getInstance(WeakReference(applicationContext)).login(guestId)
        Hansel.getUser().setUserId(guestId)
        goToMain(guestId)
    }

    // ── Navigate to MainActivity ───────────────────────────────────────────
    private fun goToMain(uname: String) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("uname", uname)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
