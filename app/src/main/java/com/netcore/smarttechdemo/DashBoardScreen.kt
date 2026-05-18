package com.netcore.smarttechdemo

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import io.hansel.hanselsdk.Hansel
import io.hansel.ujmtracker.HanselTracker
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashBoardScreen : AppCompatActivity() {

    private lateinit var tvFcmToken: TextView
    private lateinit var tvGuid: TextView
    private lateinit var tvAddToWishList: TextView
    private lateinit var tvAddToCart: TextView
    private lateinit var tvCheckout: TextView
    private lateinit var tvUpdateProfile: TextView
    private lateinit var tvClearIdentity: TextView
    private lateinit var tvLogout: TextView
    private lateinit var tvAppInbox: TextView
    private lateinit var tvCustomAppInbox: TextView
    private lateinit var tvSetLocation: TextView
    private lateinit var tvWebView: TextView
    private lateinit var tvListInList: TextView
    private lateinit var switchPushNotifications: SwitchCompat
    private lateinit var switchInAppMessages: SwitchCompat
    private lateinit var switchTracking: SwitchCompat
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 101
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_screen)
        supportActionBar?.hide()

        initializeViews()
        initializeSwitches()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setClickListeners()
    }

    private fun initializeViews() {
        tvFcmToken = findViewById(R.id.tv_fcm_token)
        tvGuid = findViewById(R.id.tv_guid)
        tvAddToWishList = findViewById(R.id.tv_add_to_wish_list)
        tvAddToCart = findViewById(R.id.tv_add_to_cart)
        tvCheckout = findViewById(R.id.tv_checkout)
        tvUpdateProfile = findViewById(R.id.tv_update_profile)
        tvClearIdentity = findViewById(R.id.tv_clear_identity)
        tvLogout = findViewById(R.id.tv_logout)
        tvAppInbox = findViewById(R.id.tv_appinox)
        tvCustomAppInbox = findViewById(R.id.tv_customappinox)
        tvSetLocation = findViewById(R.id.tv_set_location)
        tvWebView = findViewById(R.id.tv_webview)
        tvListInList = findViewById(R.id.tv_list_in_list)
        switchPushNotifications = findViewById(R.id.sw_opt_pn)
        switchInAppMessages = findViewById(R.id.sw_opt_in_app)
        switchTracking = findViewById(R.id.sw_opt_tracking)
        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        // Show current FCM token and GUID
        tvFcmToken.text = SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()
        tvGuid.text = Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()
    }

    private fun setClickListeners() {
        tvFcmToken.setOnClickListener { copyFcmToken() }
        tvGuid.setOnClickListener { copyDeviceGuid() }
        tvAddToWishList.setOnClickListener { trackAddToWishList() }
        tvAddToCart.setOnClickListener { trackAddToCart() }
        tvCheckout.setOnClickListener { trackCheckout() }
        tvUpdateProfile.setOnClickListener { updateProfile() }
        tvClearIdentity.setOnClickListener { clearIdentity() }
        tvLogout.setOnClickListener { logoutUser() }
        tvAppInbox.setOnClickListener { openAppInbox() }
        tvCustomAppInbox.setOnClickListener { openCustomAppInbox() }
        tvSetLocation.setOnClickListener { setLocation() }
        tvWebView.setOnClickListener { startActivity(Intent(this, WebActivity::class.java)) }
        tvListInList.setOnClickListener { startActivity(Intent(this, ListInListActivity::class.java)) }
    }

    private fun copyFcmToken() {
        val token = SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()
        copyToClipboard("Device Push Token", token)
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun copyDeviceGuid() {
        val guid = Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()
        copyToClipboard("Device GUID", guid)
        Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show()
    }

    private fun trackAddToWishList() {
        val payload = hashMapOf<String, Any>(
            "name" to "Mobile",
            "prid" to 2,
            "price" to 15000.00,
            "color" to "red",
            "quantity" to "2",
            "datetest1" to "2023-09-13 18:09:00",
            "datetest2" to SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        )
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("Add To Wishlist", payload)
        Toast.makeText(this, getString(R.string.tracking_add_to_wish_list), Toast.LENGTH_SHORT).show()

        // Request notification permission and report result to SDK
        SmartPush.getInstance(WeakReference(this)).requestNotificationPermission(
            object : SMTNotificationPermissionCallback {
                override fun notificationPermissionStatus(status: Int) {
                    val normalised = if (status == SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED)
                        SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED
                    else
                        SMTPNPermissionConstants.SMT_PN_PERMISSION_DENIED
                    SmartPush.getInstance(WeakReference(this@DashBoardScreen)).updateNotificationPermission(normalised)
                }
            }
        )
    }

    private fun trackAddToCart() {
        val payload = hashMapOf<String, Any>(
            "name" to "T-shirt",
            "prid" to 2,
            "price" to 15000.00,
            "size" to "xl",
            "color" to "Red"
        )
        val smartech = Smartech.getInstance(WeakReference(applicationContext))
        smartech.trackEvent("Add To Cart", payload)
        smartech.trackEvent("subscription_checkout_page", payload)
        Toast.makeText(this, getString(R.string.tracking_add_to_cart), Toast.LENGTH_SHORT).show()
    }

    private fun trackCheckout() {
        val payload = hashMapOf<String, Any>(
            "name" to "Mobile",
            "prid" to 2,
            "price" to 15000.00,
            "color" to "red",
            "quantity" to "2"
        )
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("Checkout", payload)
        Toast.makeText(this, getString(R.string.tracking_checkout), Toast.LENGTH_SHORT).show()

        // Update profile with virtual card flag after checkout
        Handler(Looper.getMainLooper()).postDelayed({
            Smartech.getInstance(WeakReference(applicationContext))
                .updateUserProfile(hashMapOf("VIRTUAL_CARD_ENABLED" to "yes"))
        }, 1000)

        // Enrich with Hansel A/B experiment data
        val hanselProps = hashMapOf<String, Any>("name" to "test")
        val hanselData = HanselTracker.logEvent("HanselTracker", "smt", hanselProps)
        hanselData?.let { hanselProps.putAll(it) }

        // Post-payment funnel event
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent(
            "post_payment_pop_up",
            hashMapOf("subscription_type" to "STANDARD")
        )
    }

    private fun updateProfile() {
        val identity = Smartech.getInstance(WeakReference(this)).getUserIdentity()
        if (!identity.isNullOrEmpty()) {
            startActivity(Intent(this, UpdateProfileScreen::class.java))
        } else {
            Toast.makeText(this, getString(R.string.please_login_first), Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearIdentity() {
        SharedPreferenceHelper.putBoolean(this, Keys.IS_USER_LOGGED_IN, false)
        SharedPreferenceHelper.putString(this, Keys.LOGGED_IN_USER_IDENTITY, null)
        Smartech.getInstance(WeakReference(applicationContext)).clearUserIdentity()
        Toast.makeText(this, getString(R.string.user_identity_cleared), Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        preferences.edit()
            .remove(LoginScreen.KEY_EMAIL)
            .remove(LoginScreen.KEY_AUTO_LOGIN)
            .apply()
        SharedPreferenceHelper.putBoolean(this, Keys.IS_USER_LOGGED_IN, false)
        SharedPreferenceHelper.putString(this, Keys.LOGGED_IN_USER_IDENTITY, null)

        Smartech.getInstance(WeakReference(this)).logoutAndClearUserIdentity(true)
        Hansel.getUser().clear()

        Toast.makeText(this, getString(R.string.you_are_logged_out), Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
        SmartPush.getInstance(WeakReference(this)).resetNotificationDoubleOptIn()
    }

    private fun openCustomAppInbox() {
        startActivity(Intent(this, CustomInbox::class.java))
    }

    private fun setLocation() {
        if (checkPermissions()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Smartech.getInstance(WeakReference(applicationContext)).setUserLocation(location)
                    Toast.makeText(
                        this, "Location set: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Location unavailable. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkPermissions(): Boolean {
        val fineOk = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val bgOk = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        return fineOk && bgOk
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeSwitches() {
        val smartPush = SmartPush.getInstance(WeakReference(this))
        val smartech = Smartech.getInstance(WeakReference(this))

        switchPushNotifications.isChecked = smartPush.hasOptedPushNotification()
        switchInAppMessages.isChecked = smartech.hasOptedInAppMessage()
        switchTracking.isChecked = smartech.hasOptedTracking()

        switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            smartPush.optPushNotification(isChecked)
        }
        switchInAppMessages.setOnCheckedChangeListener { _, isChecked ->
            smartech.optInAppMessage(isChecked)
        }
        switchTracking.setOnCheckedChangeListener { _, isChecked ->
            smartech.optTracking(isChecked)
        }
    }

    private fun copyToClipboard(label: String, text: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}
