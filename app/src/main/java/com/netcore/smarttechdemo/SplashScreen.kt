package com.netcore.smarttechdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.netcore.android.Smartech
import io.hansel.hanselsdk.Hansel

import java.lang.ref.WeakReference
import android.os.Looper


class SplashScreen : AppCompatActivity() {

    private val splashScreenDelay: Long = 2000
    private var isDeeplinkHandled = false  // Prevent overlapping

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        supportActionBar?.hide()

        // Pair test device
        Hansel.pairTestDevice(intent.dataString)


        // Smartech deeplink handling
        Smartech.getInstance(WeakReference(this)).isDeepLinkFromSmartech(intent)

        // Navigate to the login screen after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLoginScreen()
        }, splashScreenDelay)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }
}




















































/*class SplashScreen : AppCompatActivity() {

    private val splashScreenDelay: Long = 2000
    private var isDeeplinkHandled = false  // ✅ FLAG to prevent overlapping

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        supportActionBar?.hide()

        Hansel.pairTestDevice(intent.dataString)
        Smartech.getInstance(WeakReference(this)).isDeepLinkFromSmartech(intent)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        isDeeplinkHandled = checkAndHandleStoredDeeplink()  // ✅ set flag before delay

        Handler().postDelayed({
            if (!isDeeplinkHandled) {
                navigateToLoginScreen()
            }
        }, splashScreenDelay)
    }

    private fun navigateToLoginScreen() {
        val intent = Intent(this, LoginScreen::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkAndHandleStoredDeeplink(): Boolean {
        val sharedPref = getSharedPreferences("DeeplinkPrefs", Context.MODE_PRIVATE)
        val deepLinkValue = sharedPref.getString("DEEPLINK_URL", null)
        val customPayload = sharedPref.getString("CUSTOM_PAYLOAD", null)

        return when {
            !deepLinkValue.isNullOrEmpty() -> {
                sharedPref.edit().remove("DEEPLINK_URL").apply()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkValue))
                startActivity(intent)
                finish()
                true
            }

            !customPayload.isNullOrEmpty() -> {
                sharedPref.edit().remove("CUSTOM_PAYLOAD").apply()
                handleCustomPayload(customPayload)
                true
            }

            else -> false
        }
    }

    private fun handleCustomPayload(payload: String) {
        try {
            val json = JSONObject(payload)
            val screenName = json.optString("screen")

            when (screenName.lowercase()) {
                "profile" -> startActivity(Intent(this, RegisterScreen::class.java))
                "login" -> startActivity(Intent(this, LoginScreen::class.java))
                else -> Log.w("Harish Deeplink", "Unknown screen in payload: $screenName")
            }
            finish()  // ✅ Don't forget to finish splash screen
        } catch (e: JSONException) {
            Log.e("Harish Deeplink", "Invalid JSON payload", e)
            navigateToLoginScreen()
        }
    }
}*/


























/*class SplashScreen : AppCompatActivity() {

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
}*/



















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
