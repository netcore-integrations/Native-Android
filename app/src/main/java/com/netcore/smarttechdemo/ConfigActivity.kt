package com.netcore.smarttechdemo

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppConfigPrefs", MODE_PRIVATE)

        // Get references to UI elements
        val etSmtAppId = findViewById<EditText>(R.id.et_smt_app_id)
        val etHanselAppId = findViewById<EditText>(R.id.et_hansel_app_id)
        val etHanselAppKey = findViewById<EditText>(R.id.et_hansel_app_key)
        val btnSave = findViewById<Button>(R.id.btn_save)

        // Preload values from SharedPreferences or manifest meta-data
        etSmtAppId.setText(ConfigUtils.getConfigValue(this, "SMT_APP_ID"))
        etHanselAppId.setText(ConfigUtils.getConfigValue(this, "HANSEL_APP_ID"))
        etHanselAppKey.setText(ConfigUtils.getConfigValue(this, "HANSEL_APP_KEY"))

        // Save updated values on button click
        btnSave.setOnClickListener {
            val smtAppId = etSmtAppId.text.toString()
            val hanselAppId = etHanselAppId.text.toString()
            val hanselAppKey = etHanselAppKey.text.toString()

            if (smtAppId.isBlank() || hanselAppId.isBlank() || hanselAppKey.isBlank()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save new values to SharedPreferences
            sharedPreferences.edit().apply {
                putString("SMT_APP_ID", smtAppId)
                putString("HANSEL_APP_ID", hanselAppId)
                putString("HANSEL_APP_KEY", hanselAppKey)
                apply()
            }

            Toast.makeText(this, "Appid's saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }



}



