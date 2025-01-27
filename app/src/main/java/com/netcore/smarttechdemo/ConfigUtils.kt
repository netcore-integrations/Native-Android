package com.netcore.smarttechdemo

import android.content.Context
import android.content.pm.PackageManager

object ConfigUtils {
    fun getConfigValue(context: Context, key: String): String {
        val sharedPreferences = context.getSharedPreferences("AppConfigPrefs", Context.MODE_PRIVATE)
        val savedValue = sharedPreferences.getString(key, null)

        // If no value is saved in SharedPreferences, fallback to manifest meta-data
        return savedValue ?: try {
            val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val metaData = applicationInfo.metaData
            metaData?.getString(key) ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
