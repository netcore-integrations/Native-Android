package com.netcore.smarttechdemo

import android.content.Intent
import android.os.Bundle
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

        //Initialise Views
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

        sdkRelases.setOnClickListener {
            startActivity(Intent(this, DeviceInfoActivity::class.java))
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
    }
}
