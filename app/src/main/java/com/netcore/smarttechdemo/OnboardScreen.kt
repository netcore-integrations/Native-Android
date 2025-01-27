package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton

class OnboardScreen : AppCompatActivity() {

    private lateinit var appIdButton: CardView
    private lateinit var sdkRelases: CardView
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboard_screen)

        // Initialize Views
        appIdButton = findViewById(R.id.dash_appid)
        sdkRelases=findViewById(R.id.sdk_releases)
        loginButton = findViewById(R.id.login_id)

        // Set Click Listeners
        appIdButton.setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            navigateToLogin()
        }

        sdkRelases.setOnClickListener(){
            val url = "https://developer.netcorecloud.com/docs/android-release-notes"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
    }
}
