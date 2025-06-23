package com.netcore.smarttechdemo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.netcore.android.smartechpush.SmartPush
import java.lang.ref.WeakReference

class Firebase_Service : FirebaseMessagingService() {
    // fetching push token and passing Smartech dashboard
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("fcmtoken", token)
        SmartPush.getInstance(WeakReference(this)).setDevicePushToken(token)
    }

    // for fetching Firebase remote message data
   override fun onMessageReceived(remoteMessage: RemoteMessage) {
       super.onMessageReceived(remoteMessage)
        val isPnHanledBySmartech:Boolean = SmartPush.getInstance(WeakReference(applicationContext)).handleRemotePushNotification( remoteMessage)
        if (!isPnHanledBySmartech){
            // Notification from other sources, handle yourself
        }
   }




}