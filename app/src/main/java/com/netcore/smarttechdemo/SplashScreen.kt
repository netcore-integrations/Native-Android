package com.netcore.smarttechdemo

import android.content.Intent
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
            handleDeepLink() // Implement your deeplink handling logic here
        }




        // Make the activity fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )



        // Use a Handler to delay the transition to the Login screen
        Handler().postDelayed({
            navigateToLoginScreen()
        }, splashScreenDelay)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun handleDeepLink() {
        // Implement your deep link handling logic here
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
