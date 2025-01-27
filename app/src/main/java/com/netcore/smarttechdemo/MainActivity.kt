package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {


    private lateinit var btnCe: CardView
    private lateinit var btnpx: CardView


    //android 13 permissons code for android 13 and versions

    private val notificationPermissionCallback = object : SMTNotificationPermissionCallback {
        override fun notificationPermissionStatus(status: Int) {
            if (status == SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED) {
                // Handle the status when permission is granted
            } else {
                // Handle the status when permission is denied
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//android 13 permissons code for android 13 and versions intializations
        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext)).updateNotificationPermission()

        initUI() // Initialize UI elements




        // Navigate ce dash board screen
        btnCe.setOnClickListener {
            val intent = Intent(this, DashBoardScreen::class.java)
            startActivity(intent)
        }

   // Naviagate to product experience screen
        btnpx.setOnClickListener {
            startActivity(Intent(this, ProductExperienceDashBoard::class.java))
        }


    }



    private fun initUI() {
        btnCe = findViewById(R.id.btn_ce)
        btnpx = findViewById(R.id.btn_px)


    }
}




































































































