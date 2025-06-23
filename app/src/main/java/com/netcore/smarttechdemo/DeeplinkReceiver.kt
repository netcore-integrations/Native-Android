package com.netcore.smarttechdemo


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.netcore.android.SMTBundleKeys
import org.json.JSONException
import org.json.JSONObject

class DeeplinkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            if (context == null) {
                Log.e("Harish Deeplink", "Context is null")
                return
            }

            val bundle = intent?.extras
            if (bundle == null) {
                Log.e("Harish Deeplink", "Intent extras are null")
                return
            }

            val deepLinkSource = bundle.getString(SMTBundleKeys.SMT_KEY_DEEPLINK_SOURCE)
            val deepLinkValue = bundle.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)
            val customPayload = bundle.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)

            Log.v("Harish Deeplink", "Deeplink: $deepLinkValue, Payload: $customPayload, Source: $deepLinkSource")

            when (deepLinkSource) {
                "InAppMessage" -> {
                    if (!deepLinkValue.isNullOrEmpty() && deepLinkValue.contains("deeplink-click", ignoreCase = true)) {
                        handleCustomPayload(context, customPayload)
                    }
                }

                "PushNotification" -> {
                    handleCustomPayload(context, customPayload)

                    if (!deepLinkValue.isNullOrEmpty()) {
                        handleDeeplink(context, deepLinkValue)
                    }
                }

                else -> {
                    Log.w("Harish Deeplink", "Unknown source: $deepLinkSource")
                }
            }

        } catch (e: Exception) {
            Log.e("Harish Deeplink", "Exception in onReceive", e)
        }
    }

    private fun handleCustomPayload(context: Context, customPayload: String?) {
        if (customPayload.isNullOrEmpty()) {
            Log.w("Harish Deeplink", "Custom payload is null or empty")
            return
        }

        try {
            val json = JSONObject(customPayload)
            val screenName = json.optString("screen")

            when (screenName.lowercase()) {
                "profile" -> openProfile(context)
                "login" -> openLogin(context)
                else -> Log.w("Harish Deeplink", "Unknown screen in payload: $screenName")
            }

        } catch (e: JSONException) {
            Log.e("Harish Deeplink", "Invalid custom payload JSON", e)
        } catch (e: Exception) {
            Log.e("Harish Deeplink", "Error while handling custom payload", e)
        }
    }

    private fun handleDeeplink(context: Context, deepLinkValue: String) {
        try {
            val uri: Uri = deepLinkValue.toUri()
            val scheme = uri.scheme ?: ""
            val host = uri.host ?: ""
            val full = "$scheme://$host"

            when (full.lowercase()) {
                "sampleapp://profile" -> openProfile(context)
                "sampleapp://login" -> openLogin(context)
                else -> {
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Log.e("Harish Deeplink", "Error while handling deeplink", e)
        }
    }

    private fun openProfile(context: Context) {
        try {
            val intent = Intent(context, RegisterScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("Harish Deeplink", "Error opening profile screen", e)
        }
    }

    private fun openLogin(context: Context) {
        try {
            val intent = Intent(context, LoginScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("Harish Deeplink", "Error opening login screen", e)
        }
    }
}
















































/*
class DeeplinkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent?.extras == null) return

        val bundle = intent.extras
        val deepLinkSource = bundle?.getString(SMTBundleKeys.SMT_KEY_DEEPLINK_SOURCE)
        val deepLinkValue = bundle?.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)
        val customPayload = bundle?.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)



        Log.v("Harish Deeplink", "Deeplink: $deepLinkValue, Payload: $customPayload,deeplink source $deepLinkSource")


       if (deepLinkSource == "InAppMessage") {
           if (deepLinkValue == "deeplink-click") {
               // 1️⃣ Handle key-value deeplink (custom payload)
               if (!customPayload.isNullOrEmpty()) {
                   try {
                       val json = JSONObject(customPayload)
                       val screenName = json.optString("screen")

                       when (screenName.lowercase()) {
                           "profile" -> openProfile(context)
                           "login" -> openLogin(context)
                           // Add more screens here if needed
                       }
                   } catch (e: Exception) {
                       Log.e("Harish Deeplink", "Invalid custom payload JSON", e)
                   }
               }

               // 2️⃣ Handle direct deeplink or external link


           }
       }

        else if (deepLinkSource == "PushNotification"){

           if (!customPayload.isNullOrEmpty()) {
               try {
                   val json = JSONObject(customPayload)
                   val screenName = json.optString("screen")

                   when (screenName.lowercase()) {
                       "profile" -> openProfile(context)
                       "login" -> openLogin(context)
                       // Add more screens here if needed
                   }
               } catch (e: Exception) {
                   Log.e("Harish Deeplink", "Invalid custom payload JSON", e)
               }
           }

           // 2️⃣ Handle direct deeplink or external link


           if (!deepLinkValue.isNullOrEmpty()) {
               handleDeeplink(context, deepLinkValue)
           }
       }




    }


    private fun handleDeeplink(context: Context, deepLinkValue: String) {
        val uri = Uri.parse(deepLinkValue)
        val scheme = uri.scheme ?: ""
        val host = uri.host ?: ""
        val full = "$scheme://$host"

        when (full.lowercase()) {
            "sampleapp://profile" -> openProfile(context)
            "sampleapp://login" -> openLogin(context)

            else -> {
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK

                }
                context.startActivity(intent)


            }
        }
    }

    private fun openProfile(context: Context) {
        val intent = Intent(context, RegisterScreen::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun openLogin(context: Context) {
        val intent = Intent(context, LoginScreen::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
*/



























/*class DeeplinkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.let { bundleExtra ->
            val customPayloadString = bundleExtra.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)
            val deepLinkValue = bundleExtra.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)

            if (!customPayloadString.isNullOrEmpty()) {
                try {
                    val jsonObject = JSONObject(customPayloadString)
                    val screenName = jsonObject.optString("screen")

                    if (screenName.equals("profile", ignoreCase = true)) {
                        val profileIntent = Intent(context, RegisterScreen::class.java)
                        profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context?.startActivity(profileIntent)
                    }

                    // You can handle other screens similarly
                    // else if (screenName == "home") { ... }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }





            val isFromBg = bundleExtra.getBoolean(SMTBundleKeys.SMT_KEY_IS_DEEPLINK_FROM_BG, false)

            if (!deepLinkValue.isNullOrEmpty()) {
                if (isFromBg) {
                    // Store the deep link to handle it later in SplashScreen
                    context?.let {
                        val sharedPref = it.getSharedPreferences("DeeplinkPrefs", Context.MODE_PRIVATE)
                        sharedPref.edit().putString("DEEPLINK_URL", deepLinkValue).apply()
                    }
                } else {
                    // Handle the deep link immediately
                    handleDeeplink(context, deepLinkValue)
                }
            }
        }
    }

    private fun handleDeeplink(context: Context?, deeplink: String) {
        when (deeplink) {
            "sampleapp://profile" -> openProfile(context)
            "sampleapp://login" -> openLogin(context)
            else -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context?.startActivity(intent)
            }
        }
    }

    private fun openProfile(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, RegisterScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }

    private fun openLogin(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, LoginScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }
}*/






























    /*override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.let { bundleExtra ->

            // Check if the deeplink was triggered from the background
            val isFromBg = bundleExtra.getBoolean(SMTBundleKeys.SMT_KEY_IS_DEEPLINK_FROM_BG, false)

            // Extract deep link value
            val deepLinkValue = bundleExtra.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)
            val customPayload = bundleExtra.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)


            Log.v("Harish Deeplink", "Deeplink: $deepLinkValue, Payload: $customPayload, IsFromBG: $isFromBg")
      //"screen": "profile"
            if (!customPayload.isNullOrEmpty()) {
                try {
                    val jsonObject = JSONObject(customPayload)
                    val screenName = jsonObject.optString("screen")

                    if (screenName.equals("profile", ignoreCase = true)) {
                        val profileIntent = Intent(context, UpdateProfileScreen::class.java)
                        profileIntent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context?.startActivity(profileIntent)
                    }

                    // You can handle other screens similarly
                    // else if (screenName == "home") { ... }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            if (!deepLinkValue.isNullOrEmpty()) {
                if (isFromBg) {
                    // Store the deep link to handle it later in SplashScreen
                    context?.let {
                        val sharedPref = it.getSharedPreferences("DeeplinkPrefs", Context.MODE_PRIVATE)
                        sharedPref.edit().putString("DEEPLINK_URL", deepLinkValue).apply()
                    }
                } else {
                    // Handle the deep link immediately
                    handleDeeplink(context, deepLinkValue)
                }
            }
        }
    }

    private fun handleDeeplink(context: Context?, deeplink: String) {
        when (deeplink) {
            "sampleapp://profile" -> openProfile(context)
            "sampleapp://login" -> openLogin(context)
            else -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context?.startActivity(intent)
            }
        }
    }

    private fun openProfile(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, RegisterScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }

    private fun openLogin(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, LoginScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }*/































/*class DeeplinkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.extras?.let { bundleExtra ->
            // Check if the bundle contains a deeplink key
            if (bundleExtra.containsKey(SMTBundleKeys.SMT_BUNDLE_KEY_CLICK_DEEPLINK)) {

                // Normal deeplink and external web site url redirection code
                val deepLinkValue = bundleExtra.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)

                // key and values  deeplink redirection code
                val customPayload = bundleExtra.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)

                Log.v("Harish Deeplink", "Deeplink: $deepLinkValue, Payload: $customPayload")

                deepLinkValue?.let { link ->
                    if (link.isNotEmpty()) {
                        handleDeeplink(context, link)
                    }
                }
            }
        }
    }


    //  deeplink and external web site url redirection logic code
    private fun handleDeeplink(context: Context?, deeplink: String) {
        when (deeplink) {
            "sampleapp://profile" -> openProfile(context)
            "sampleapp://login" -> openLogin(context)
            else -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context?.startActivity(intent)
            }
        }
    }

    private fun openProfile(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, RegisterScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }

    private fun openLogin(context: Context?) {
        context?.let {
            val displayScreen = Intent(it, LoginScreen::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            it.startActivity(displayScreen)
        }
    }
}*/
