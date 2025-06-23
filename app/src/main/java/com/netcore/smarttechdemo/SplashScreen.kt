package com.netcore.smarttechdemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.netcore.android.Smartech
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference


class SplashScreen : AppCompatActivity() {

    private val splashScreenDelay: Long = 2000 // Duration of the splash screen in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        supportActionBar?.hide()

        // Pair the test device with the Hansel SDK, using the data string from the intent if available
        Hansel.pairTestDevice(intent.dataString)
        // Handle deeplink if it comes from Smartech
        val isSmartechHandledDeeplink = Smartech.getInstance(WeakReference(this)).isDeepLinkFromSmartech(intent)
        if (!isSmartechHandledDeeplink) {

        }




        // Make the activity fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )



        // Use a Handler to delay the transition to the Login screen
        Handler().postDelayed({

            //checkAndHandleStoredDeeplink()
            navigateToLoginScreen()
        }, splashScreenDelay)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }




    private fun checkAndHandleStoredDeeplink() {
        val sharedPref = getSharedPreferences("DeeplinkPrefs", Context.MODE_PRIVATE)
        val deepLinkValue = sharedPref.getString("DEEPLINK_URL", null)

        if (!deepLinkValue.isNullOrEmpty()) {
            sharedPref.edit().remove("DEEPLINK_URL").apply() // Clear after using
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkValue))
            startActivity(intent)

        }
    }
}



















/*
package com.netcore.smarttechdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.netcore.android.Smartech
import java.lang.ref.WeakReference


class SplashScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)


        val isSmartechHandledDeeplink = Smartech.getInstance(WeakReference(this)).isDeepLinkFromSmartech(intent)
        if (!isSmartechHandledDeeplink) {
            //Handle deeplink on app side
        }

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}*/
