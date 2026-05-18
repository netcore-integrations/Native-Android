package com.netcore.smarttechdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.netcore.android.SMTBundleKeys

class DeeplinkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras ?: return

        val deepLinkValue = bundle.getString(SMTBundleKeys.SMT_KEY_DEEPLINK)
        val customPayload = bundle.getString(SMTBundleKeys.SMT_KEY_CUSTOM_PAYLOAD)

        Log.v(TAG, "deeplink=$deepLinkValue  payload=$customPayload")

        if (!deepLinkValue.isNullOrEmpty()) {
            handleDeeplink(context, deepLinkValue)
        }

        if (!customPayload.isNullOrEmpty()) {
            handleCustomPayload(context, customPayload)
        }
    }

    private fun handleDeeplink(context: Context?, deeplink: String) {
        val activityIntent: Intent = when {
            deeplink.startsWith("sampleapp://profile") ->
                Intent(context, UpdateProfileScreen::class.java)
            deeplink.startsWith("sampleapp://login") ->
                Intent(context, LoginScreen::class.java)
            deeplink.startsWith("sampleapp://register") ->
                Intent(context, RegisterScreen::class.java)
            deeplink.startsWith("https://sampleapp.com/login") ->
                Intent(context, LoginScreen::class.java)
            deeplink.startsWith("https://sampleapp.com/register") ->
                Intent(context, RegisterScreen::class.java)
            else -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                    .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                context?.startActivity(browserIntent)
                return
            }
        }
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(activityIntent)
    }

    private fun handleCustomPayload(context: Context?, payload: String) {
        try {
            val model = Gson().fromJson(payload, DeepLinkKeyValueModel::class.java) ?: return
            val page = model.page ?: return

            val activityIntent = when {
                page.contains("dashboard") -> Intent(context, DashBoardScreen::class.java)
                page.contains("profile") -> Intent(context, UpdateProfileScreen::class.java)
                else -> {
                    Log.v(TAG, "Custom payload page not recognised: $page")
                    return
                }
            }
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(activityIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse custom payload: $payload", e)
        }
    }

    companion object {
        private const val TAG = "DeeplinkReceiver"
    }
}
