package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.netcore.android.Smartech
import com.netcore.android.contentpz.SMTWidgetListener
import com.netcore.android.contentpz.model.SMTWidget
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import java.lang.ref.WeakReference

import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private lateinit var btnCe: CardView
    private lateinit var btnPx: CardView
    private lateinit var widgetContainer: LinearLayout

    // Push notification permissions for Android 13+
    private val notificationPermissionCallback = object : SMTNotificationPermissionCallback {
        override fun notificationPermissionStatus(status: Int) {
            if (status == SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED) {
                println("✅ Notification permission granted")
            } else {
                println("❌ Notification permission denied")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the SMTWidgetListener
      //  Smartech.getInstance(WeakReference(this)).setWidgetListener(this, this)

        // Fetch widgets
      //  Smartech.getInstance(WeakReference(this)).getAllWidgets()
       // val widget = Smartech.getInstance(WeakReference(this)).getWidgetByName("testing")
       // println("widget data: $widget")

        // Android 13+ Notification permission
      /*  SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext))
            .updateNotificationPermission()*/

        // Initialize UI elements
        initUI()

        // Navigate CE dashboard screen
        btnCe.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
        }

        // Navigate to Product Experience screen
        btnPx.setOnClickListener {
            startActivity(Intent(this, ProductExperienceDashBoard::class.java))
        }
    }

    private fun initUI() {
        btnCe = findViewById(R.id.btn_ce)
        btnPx = findViewById(R.id.btn_px)
        widgetContainer = findViewById(R.id.widgetContainer)
    }

    /*override fun onWidgetsLoaded(data: HashMap<String, SMTWidget?>) {

        println("widget data$data")
        // Handle loaded widgets safely
        for ((_, widget) in data) {
            if (widget != null && widget.layoutType.equals("image", ignoreCase = true)) {
                runOnUiThread {
                    showImageWidget(widget)
                }
            } else {
                println("⚠️ Widget is null or not image type")
            }
        }
    }

    private fun showImageWidget(widget: SMTWidget) {
        val content = widget.content ?: return
        widgetContainer.visibility = View.VISIBLE
        widgetContainer.removeAllViews()

        // Image
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                500
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        Glide.with(this).load(content.mediaUrl).into(imageView)
        widgetContainer.addView(imageView)

        // Action Button
        val actionButtons = content.actionButtons
        if (!actionButtons.isNullOrEmpty()) {
            val buttonData = actionButtons[0]

            val button = Button(this).apply {
                text = buttonData.actionName ?: "Action"
                buttonData.backgroundColor?.let {
                    setBackgroundColor(android.graphics.Color.parseColor(it))
                }
                buttonData.textColor?.let {
                    setTextColor(android.graphics.Color.parseColor(it))
                }
                setPadding(24, 12, 24, 12)
            }

            button.setOnClickListener {
                buttonData.actionDeeplink?.let { deeplink ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                    startActivity(intent)
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
                gravity = android.view.Gravity.CENTER
            }
            widgetContainer.addView(button, params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to avoid memory leaks
        Smartech.getInstance(WeakReference(this)).removeWidgetListener(this)
    }*/
}































/*class MainActivity : AppCompatActivity(), SMTWidgetListener {

    private lateinit var btnCe: CardView
    private lateinit var btnpx: CardView

    // Push notification permissions for Android 13+
    private val notificationPermissionCallback = object : SMTNotificationPermissionCallback {
        override fun notificationPermissionStatus(status: Int) {
            if (status == SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the SMTWidgetListener
        Smartech.getInstance(WeakReference(applicationContext)).setWidgetListener(this,this)

        // Fetch widgets

        Smartech.getInstance(WeakReference(this)).getAllWidgets()
        val widget = Smartech.getInstance(WeakReference(this)).getWidgetByName("testing")
        println("widget data: $widget")


        // Android 13+ permissions
        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext))
            .updateNotificationPermission()

        // Initialize UI elements
        initUI()

        // Navigate CE dashboard screen
        btnCe.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
        }

        // Navigate to Product Experience screen
        btnpx.setOnClickListener {
            startActivity(Intent(this, ProductExperienceDashBoard::class.java))
        }
    }

    private fun initUI() {
        btnCe = findViewById(R.id.btn_ce)
        btnpx = findViewById(R.id.btn_px)
    }

    override fun onWidgetsLoaded(data: HashMap<String, SMTWidget?>) {
        // Handle loaded widgets safely
        for ((name, widget) in data) {
            if (widget != null) {
                println("Widget loaded: $name → ${widget}")
            } else {
                println("Widget $name is null")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to avoid memory leaks
        Smartech.getInstance(WeakReference(this)).removeWidgetListener(this)
    }
}*/






























































































/*class MainActivity : AppCompatActivity(), SMTWidgetListener {
    private lateinit var btnCe: CardView
    private lateinit var btnpx: CardView


    //push notification  permissions code for Android 13 and above versions

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

        // Set the SMTWidgetListener
        Smartech.getInstance(WeakReference(this)).setWidgetListener(
            this,
            listener = TODO()
        )

        Smartech.getInstance(WeakReference(this)).getAllWidgets()

        Smartech.getInstance(WeakReference(this)).getWidgetByName("testing")
//android 13 permissions code for android 13 and versions initialisations
        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext)).updateNotificationPermission()

        initUI() // Initialize UI elements




        // Navigate ce dashboard screen
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

    override fun onWidgetsLoaded(data: HashMap<String, SMTWidget?>) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove listener to avoid memory leaks
        Smartech.getInstance(WeakReference(this)).removeWidgetListener(this)
    }
}*/




































































































