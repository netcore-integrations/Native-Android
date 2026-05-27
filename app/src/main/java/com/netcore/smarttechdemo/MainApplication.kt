package com.netcore.smarttechdemo

import android.app.Application
import android.app.NotificationManager
import android.content.ContentValues
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.netcore.android.Smartech
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.notification.SMTNotificationOptions
import com.netcore.android.smartechpush.notification.channel.SMTNotificationChannel
import io.hansel.core.logger.HSLLogLevel
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference


class MainApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        //Initialise the Smartech SDK
        /*val smartech = Smartech.getInstance(WeakReference(applicationContext))
        smartech.initializeSdk(
            this,
            ConfigUtils.getConfigValue(this, "SMT_APP_ID"),
            ConfigUtils.getConfigValue(this, "HANSEL_APP_ID"),
            ConfigUtils.getConfigValue(this, "HANSEL_APP_KEY")
        )*/

        val smartech = Smartech.getInstance(WeakReference(applicationContext))
        smartech.initializeSdk(this)
        smartech.setDebugLevel(9)
        smartech.trackAppInstallUpdateBySmartech()


        // enable px sdk logs
        Hansel.enableDebugLogs()
        HSLLogLevel.all.setEnabled(true)
        HSLLogLevel.mid.setEnabled(true)
        HSLLogLevel.debug.setEnabled(true)

        setupNudgeFont()

        //double opt-in push notification option
       // SmartPush.getInstance(WeakReference(this)).initiateNotificationDoubleOptIn()
        //SmartPush.getInstance(WeakReference(this)).showInstantNotificationDoubleOptIn()

        // Fetch the FCM token
        fetchFcmToken()
        // Register the DeeplinkReceiver
        registerDeeplinkReceiver()
        // Set up notification options
        setupNotificationOptions()
        setupNotificationSound()
    }

    /**
     * Configures the font applied to all Hansel nudge text.
     *
     * Two approaches are available — use only one at a time:
     *
     * Approach 1 — setAppFont(name):
     *   References a font already available on the device or registered in the app.
     *   No extra files needed. Pass "" to reset back to the Hansel SDK default.
     *   Font source  : system / app-registered font by name
     *   Setup needed : none
     *   Reset option : Yes — Hansel.setAppFont("")
     *
     * Approach 2 — setTypeface(typeface):
     *   Loads a bundled .ttf file from src/main/assets/ and passes the Typeface directly.
     *   Use this when you need a specific custom font not guaranteed to be on the device.
     *   Font source  : your bundled .ttf asset file
     *   Setup needed : place the .ttf inside app/src/main/assets/
     *   Reset option : none built-in — re-call with the desired Typeface to change it
     */
    private fun setupNudgeFont() {
        // --- Approach 1: App / system font by name ---
        // Applies a font available on the device or registered in the app by its family name.
        // Examples: "Roboto", "sans-serif-medium", "monospace"
        // Hansel.setAppFont("Roboto")
        // To reset to the Hansel SDK default font:
        // Hansel.setAppFont("")

        // --- Approach 2: Custom bundled .ttf from assets/ (active) ---
        // Font files are located in app/src/main/assets/ubuntu/
        // Available weights: Ubuntu-Regular.ttf, Ubuntu-Light.ttf, Ubuntu-Medium.ttf,
        //                    Ubuntu-Bold.ttf, and their *Italic variants.
        // Download from: https://fonts.google.com/specimen/Ubuntu
        try {
            val customTypeface = Typeface.createFromAsset(assets, "roboto/Roboto-Italic.ttf")
            Hansel.setTypeface(customTypeface)
        } catch (e: Exception) {
            Log.e("MainApplication", "Failed to load custom font for Hansel: ${e.message}")
        }
    }

    private fun setupNotificationSound() {
        // Create a notification channel group
        SmartPush.getInstance(WeakReference(applicationContext))
            .createNotificationChannelGroup("customSoundGroup", "Harish Group")

        // Build a notification channel
        val smtBuilder: SMTNotificationChannel.Builder = SMTNotificationChannel.Builder(
            "smartech", // Channel ID
            "Netcore Channel", // Channel Name
            NotificationManager.IMPORTANCE_MAX // Importance level
        )

        // Set channel description
        smtBuilder.setChannelDescription("This is Harish's notification channel")

        // Set channel group ID (make sure the group is created beforehand)
        smtBuilder.setChannelGroupId("customSoundGroup")

        // Set custom notification sound (sound file should be in `res/raw` folder without extension)
        smtBuilder.setNotificationSound("lau")

        // Build the notification channel
        val smtNotificationChannel: SMTNotificationChannel = smtBuilder.build()

        // Create the notification channel
        SmartPush.getInstance(WeakReference(applicationContext))
            .createNotificationChannel(smtNotificationChannel)
    }

    private fun registerDeeplinkReceiver() {
        val deeplinkReceiver = DeeplinkReceiver()
        val filter = IntentFilter("com.smartech.EVENT_PN_INBOX_CLICK")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            applicationContext.registerReceiver(deeplinkReceiver, filter, RECEIVER_EXPORTED)
        } else {
            ContextCompat.registerReceiver(
                applicationContext,
                deeplinkReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

    }

    private fun setupNotificationOptions() {
        val options = SMTNotificationOptions(this).apply {
            brandLogo = "pn_icon_transparent_mi" // e.g. logo is a sample name for brand logo
           // largeIcon = "pn_icon_transparent_mi" // e.g. icon_notification is a sample name for large icon
         //   smallIcon = "pn_icon_transparent_mi" // e.g. ic_action_play is a sample name for small icon
            smallIconTransparent = "transparent" // e.g. ic_action_play is a sample name for transparent small icon
            transparentIconBgColor = "#000000"
            placeHolderIcon = "pn_icon_transparent_mi" // e.g. ic_notification is a sample name for placeholder icon
        }

        SmartPush.getInstance(WeakReference(this)).setNotificationOptions(options)
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful && !TextUtils.isEmpty(task.result)) {
                task.result?.let { handleFcmToken(it) }
            } else {
                Log.e(ContentValues.TAG, "Fetch FCM token failed: Task unsuccessful or token is empty.")
            }
        })
    }

    private fun handleFcmToken(fcmToken: String) {
        val smartTech = SmartPush.getInstance(WeakReference(this))
        val currentToken = smartTech.getDevicePushToken()

        Log.i("TOKEN", "FCM Instance Id Token: $fcmToken")
        Log.i("TOKEN", "Current FCM Token: $currentToken")

        if (TextUtils.isEmpty(currentToken)) {
            smartTech.setDevicePushToken(fcmToken) // Set the FCM token if there is no current token
        } else if (currentToken != fcmToken) {
            smartTech.setDevicePushToken(fcmToken) // Update the token if it's different
            Log.i("TOKEN", "New token set: $fcmToken")
        } else {
            Log.i("TOKEN", "Both tokens are the same.")
        }
    }

}
