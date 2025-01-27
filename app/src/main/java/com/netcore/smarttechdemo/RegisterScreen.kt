package com.netcore.smarttechdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechappinbox.network.listeners.SMTInboxCallback
import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData
import com.netcore.android.smartechappinbox.utility.SMTAppInboxRequestBuilder
import com.netcore.android.smartechappinbox.utility.SMTInboxDataType
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference
class RegisterScreen : AppCompatActivity(), View.OnClickListener {
    private lateinit var textEditTextUser: EditText
    private lateinit var textEditTextPassword1: EditText
    private lateinit var textEditTextPassword2: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var linearBodyReg: LinearLayout
    private lateinit var inputValidation: InputValidation
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        // Safely hide the action bar
        supportActionBar?.hide()

        try {
            initViews()
            initListeners()
            initObjects()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Initialization error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Initialize views
    private fun initViews() {
        textEditTextUser = findViewById(R.id.user_field)
        textEditTextPassword1 = findViewById(R.id.pass_field)
        textEditTextPassword2 = findViewById(R.id.pass_field2)
        btnLogin = findViewById(R.id.log_button)
        btnRegister = findViewById(R.id.reg_button)
        linearBodyReg = findViewById(R.id.linearBodyReg)

        // Check if any of these views are null
        if (textEditTextUser == null || textEditTextPassword1 == null || textEditTextPassword2 == null ||
            btnLogin == null || btnRegister == null || linearBodyReg == null
        ) {
            throw NullPointerException("One or more views could not be initialized. Check layout IDs.")
        }
    }

    // Initialize listeners
    private fun initListeners() {
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }

    // Initialize objects
    private fun initObjects() {
        dbHelper = DbHelper(this)
        inputValidation = InputValidation(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.log_button -> {
                Toast.makeText(applicationContext, "Login screen", Toast.LENGTH_SHORT).show()
                val intentLog = Intent(applicationContext, LoginScreen::class.java)
                startActivity(intentLog)
            }
            R.id.reg_button -> postData()
        }
    }

    // Save data to DB
    private fun postData() {
        if (!inputValidation.isTextFilled(textEditTextUser, getString(R.string.error_message_nofill_name))) {
            return
        }
        if (!inputValidation.isTextFilled(textEditTextPassword1, getString(R.string.error_message_nofill_pass))) {
            return
        }
        if (!inputValidation.isTextFilled(textEditTextPassword2, getString(R.string.error_message_nofill_pass))) {
            return
        }
        if (!inputValidation.isPassMatch(textEditTextPassword1, textEditTextPassword2, getString(R.string.error_message_notmatch))) {
            return
        }

        if (dbHelper.regCheckUser(textEditTextUser.text.toString().trim())) {
            val user = User(
                name = textEditTextUser.text.toString().trim(),
                password = textEditTextPassword1.text.toString().trim()
            )
            dbHelper.addUser(user)
            // Tracking user identity post sucessful registration
            Smartech.getInstance(WeakReference(applicationContext)).login(textEditTextUser.text.toString())
            Hansel.getUser().setUserId(textEditTextUser.text.toString())


            val intentMain = Intent(applicationContext, MainActivity::class.java)
            intentMain.putExtra("uname", textEditTextUser.text.toString().trim())
            startActivity(intentMain)
        } else {
            Snackbar.make(
                linearBodyReg,
                getString(R.string.error_message_name),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}




















































































/*
class RegisterScreen: AppCompatActivity(), View.OnClickListener {
    private val activity = this@RegisterScreen
    private lateinit var textEditTextUser: EditText
    private lateinit var textEditTextPassword1: EditText
    private lateinit var textEditTextPassword2: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var linearBodyReg: LinearLayout
    private lateinit var inputValidation: InputValidation
    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_screen)

        supportActionBar!!.hide()

        initViews()
        initListeners()
        initObjects()
    }

    //Initialize views
    private fun initViews() {
        textEditTextUser = findViewById(R.id.user_field)
        textEditTextPassword1 = findViewById(R.id.pass_field)
        textEditTextPassword2 = findViewById(R.id.pass_field2)
        btnLogin = findViewById(R.id.log_button)
        btnRegister = findViewById(R.id.reg_button)
        linearBodyReg = findViewById(R.id.linearBodyReg)
    }
    //Initialize listeners
    private fun initListeners() {
        btnLogin.setOnClickListener(this)
        btnRegister.setOnClickListener(this)
    }
    //Initialize objects
    private fun initObjects() {
        dbHelper = DbHelper(activity)
        inputValidation = InputValidation(activity)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.log_button -> {
                Toast.makeText(applicationContext,"Login screen", Toast.LENGTH_SHORT).show()
             val intentLog = Intent(applicationContext, LoginScreen::class.java)
             startActivity(intentLog)

            }
            R.id.reg_button -> postData()
        }
    }

    //Save data to db
    private fun postData() {
        if (!inputValidation.isTextFilled(textEditTextUser, getString(R.string.error_message_nofill_name))) {
            return
        }
        if (!inputValidation.isTextFilled(textEditTextPassword1, getString(R.string.error_message_nofill_pass))) {
            return
        }
        if (!inputValidation.isTextFilled(textEditTextPassword2, getString(R.string.error_message_nofill_pass))) {
            return
        }
        if (!inputValidation.isPassMatch(textEditTextPassword1, textEditTextPassword2, getString(R.string.error_message_notmatch))) {
            return
        }

        if (dbHelper.regCheckUser(textEditTextUser.text.toString().trim())) {

            var user = User(name = textEditTextUser.text.toString().trim(),
                password = textEditTextPassword1.text.toString().trim())
            dbHelper.addUser(user)
            Smartech.getInstance(WeakReference(applicationContext)).login(textEditTextUser.text.toString())

            //println("Record saved $user")

            val intentMain = Intent(applicationContext, MainActivity::class.java)
            intentMain.putExtra("uname", textEditTextUser.text.toString().trim())
            startActivity(intentMain)
        }
        else {
            Snackbar.make(
                linearBodyReg,
                getString(R.string.error_message_name),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

}*/
