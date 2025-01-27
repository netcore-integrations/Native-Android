package com.netcore.smarttechdemo

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.netcore.android.smartechpush.SmartPush
import java.lang.ref.WeakReference

class Firebase_Service : FirebaseMessagingService() {


    // for fetching firebase remote message data
   override fun onMessageReceived(remoteMessage: RemoteMessage) {
       super.onMessageReceived(remoteMessage)
        val isPushFromSmartech:Boolean = SmartPush.getInstance(WeakReference(applicationContext)).isRemoteNotificationFromSmartech(remoteMessage)
      // handle smartech push notifications if condtion and other source push notifcation else conditon

       if(isPushFromSmartech){
           SmartPush.getInstance(WeakReference(applicationContext)).handleRemotePushNotification(remoteMessage)
       } else {
           //handle  Notification received from other sources (eg : friease,clevar tap)
       }
   }



    // fetching push token and passing smartech dash board
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("fcmtoken", token)
        SmartPush.getInstance(WeakReference(this)).setDevicePushToken(token)//fetch token
    }
}