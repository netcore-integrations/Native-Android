package com.netcore.smarttechdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.netcore.android.SMTBundleKeys

class DeeplinkReceiver : BroadcastReceiver() {

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
}
