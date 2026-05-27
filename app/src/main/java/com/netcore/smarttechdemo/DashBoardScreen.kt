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
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.MaterialToolbar
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

    // Device info (text-bearing — must stay TextView)
    private lateinit var tvFcmToken: TextView
    private lateinit var tvGuid: TextView

    // Action-only views (type is View — IDs now live on LinearLayout rows / MaterialCardViews)
    private lateinit var vWishlist: View
    private lateinit var vAddToCart: View
    private lateinit var vCheckout: View
    private lateinit var vUpdateProfile: View
    private lateinit var vClearIdentity: View
    private lateinit var vLogout: View
    private lateinit var vAppInbox: View
    private lateinit var vCustomAppInbox: View
    private lateinit var vSetLocation: View

    // Copy icons
    private lateinit var ivCopyFcm: View
    private lateinit var ivCopyGuid: View

    // Preferences switches
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

        setupToolbar()
        initializeViews()
        initializeSwitches()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setClickListeners()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_ce)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun initializeViews() {
        tvFcmToken            = findViewById(R.id.tv_fcm_token)
        tvGuid                = findViewById(R.id.tv_guid)
        ivCopyFcm             = findViewById(R.id.iv_copy_fcm)
        ivCopyGuid            = findViewById(R.id.iv_copy_guid)
        vWishlist             = findViewById(R.id.tv_add_to_wish_list)
        vAddToCart            = findViewById(R.id.tv_add_to_cart)
        vCheckout             = findViewById(R.id.tv_checkout)
        vUpdateProfile        = findViewById(R.id.tv_update_profile)
        vClearIdentity        = findViewById(R.id.tv_clear_identity)
        vLogout               = findViewById(R.id.tv_logout)
        vAppInbox             = findViewById(R.id.tv_appinox)
        vCustomAppInbox       = findViewById(R.id.tv_customappinox)
        vSetLocation          = findViewById(R.id.tv_set_location)
        switchPushNotifications = findViewById(R.id.sw_opt_pn)
        switchInAppMessages   = findViewById(R.id.sw_opt_in_app)
        switchTracking        = findViewById(R.id.sw_opt_tracking)
        preferences           = getSharedPreferences(LoginScreen.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        tvFcmToken.text = SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()
        tvGuid.text     = Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()
    }

    private fun setClickListeners() {
        ivCopyFcm.setOnClickListener       { copyFcmToken() }
        ivCopyGuid.setOnClickListener      { copyDeviceGuid() }
        vWishlist.setOnClickListener       { trackAddToWishList() }
        vAddToCart.setOnClickListener      { trackAddToCart() }
        vCheckout.setOnClickListener       { trackCheckout() }
        vUpdateProfile.setOnClickListener  { updateProfile() }
        vClearIdentity.setOnClickListener  { clearIdentity() }
        vLogout.setOnClickListener         { logoutUser() }
        vAppInbox.setOnClickListener       { openAppInbox() }
        vCustomAppInbox.setOnClickListener { openCustomAppInbox() }
        vSetLocation.setOnClickListener    { setLocation() }
    }

    // ── Copy helpers ──────────────────────────────────────────────────────────

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

    // ── Tracking Events ───────────────────────────────────────────────────────

    private fun trackAddToWishList() {
        val payload = hashMapOf<String, Any>(
            "name"      to "Mobile",
            "prid"      to 2,
            "price"     to 15000.00,
            "color"     to "red",
            "quantity"  to "2",
            "datetest1" to "2023-09-13 18:09:00",
            "datetest2" to SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        )
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("Add To Wishlist", payload)
        Toast.makeText(this, getString(R.string.tracking_add_to_wish_list), Toast.LENGTH_SHORT).show()

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
            "name"  to "T-shirt",
            "prid"  to 2,
            "price" to 15000.00,
            "size"  to "xl",
            "color" to "Red"
        )
        val smartech = Smartech.getInstance(WeakReference(applicationContext))
        smartech.trackEvent("Add To Cart", payload)
        smartech.trackEvent("subscription_checkout_page", payload)
        Toast.makeText(this, getString(R.string.tracking_add_to_cart), Toast.LENGTH_SHORT).show()
    }

    private fun trackLoyaltyStarsExpiring() {
        val payload = hashMapOf<String, Any>(
            "stars_count" to 250,
            "expiry_date" to "30 Jun 2026",
            "tier"        to "Gold"
        )
        Smartech.getInstance(WeakReference(applicationContext))
            .trackEvent("loyalty_stars_expiring", payload)
        Toast.makeText(this, "Loyalty stars expiry tracked", Toast.LENGTH_SHORT).show()
    }

    private fun trackCheckout() {
        val payload = hashMapOf<String, Any>(
            "name"     to "Mobile",
            "prid"     to 2,
            "price"    to 15000.00,
            "color"    to "red",
            "quantity" to "2"
        )
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("Checkout", payload)
        Toast.makeText(this, getString(R.string.tracking_checkout), Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            Smartech.getInstance(WeakReference(applicationContext))
                .updateUserProfile(hashMapOf("VIRTUAL_CARD_ENABLED" to "yes"))
        }, 1000)

        val hanselProps = hashMapOf<String, Any>("name" to "test")
        HanselTracker.logEvent("HanselTracker", "smt", hanselProps)?.let { hanselProps.putAll(it) }

        Smartech.getInstance(WeakReference(applicationContext)).trackEvent(
            "post_payment_pop_up",
            hashMapOf("subscription_type" to "STANDARD")
        )
    }

    // ── Tracking Users ────────────────────────────────────────────────────────

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

    // ── Advanced ──────────────────────────────────────────────────────────────

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
        SmartPush.getInstance(WeakReference(this)).resetNotificationDoubleOptIn()
    }

    private fun openCustomAppInbox() {
        startActivity(Intent(this, CustomInbox::class.java))
    }

    private fun setLocation() {
        if (checkPermissions()) getCurrentLocation() else requestLocationPermissions()
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
                    Toast.makeText(this, "Location set: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT).show()
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
        val bgOk   = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Preferences switches ──────────────────────────────────────────────────

    private fun initializeSwitches() {
        val smartPush = SmartPush.getInstance(WeakReference(this))
        val smartech  = Smartech.getInstance(WeakReference(this))

        switchPushNotifications.isChecked = smartPush.hasOptedPushNotification()
        switchInAppMessages.isChecked     = smartech.hasOptedInAppMessage()
        switchTracking.isChecked          = smartech.hasOptedTracking()

        switchPushNotifications.setOnCheckedChangeListener { _, isChecked -> smartPush.optPushNotification(isChecked) }
        switchInAppMessages.setOnCheckedChangeListener     { _, isChecked -> smartech.optInAppMessage(isChecked) }
        switchTracking.setOnCheckedChangeListener          { _, isChecked -> smartech.optTracking(isChecked) }
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private fun copyToClipboard(label: String, text: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    }
}
