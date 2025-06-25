package com.netcore.smarttechdemo
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
import io.hansel.hanselsdk.Hansel

import java.lang.ref.WeakReference

class DashBoardScreen : AppCompatActivity() {
    // Define your views
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
    private lateinit var switchPushNotifications: SwitchCompat
    private lateinit var switchInAppMessages: SwitchCompat
    private lateinit var switchTracking: SwitchCompat
   // private lateinit var tvHansel: TextView
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 101

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_screen)
        supportActionBar?.hide()

        val isSmartechHandledDeeplink = Smartech.getInstance(WeakReference(this)).isDeepLinkFromSmartech(intent)
        if (!isSmartechHandledDeeplink) {
            //Handle deeplink on app side
        }

        // Initialize all views
        initializeViews()
        // Initialize method for switches
        initializeSwitches()

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listeners
        setClickListeners()
    }



    //initalzation view
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
        switchPushNotifications = findViewById(R.id.sw_opt_pn)
        switchInAppMessages = findViewById(R.id.sw_opt_in_app)
        switchTracking = findViewById(R.id.sw_opt_tracking)

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
    }


    // onclick for button
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


        //setting guid and push token to  text view
        val pushtoken= SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()
        tvFcmToken.setText(pushtoken)

        val guid=Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()
        tvGuid.setText(guid)
    }


   // push token tracking code

    private fun copyFcmToken() {
        // Get the FCM token
        val pushtoken = SmartPush.getInstance(WeakReference(applicationContext)).getDevicePushToken()

        // Copy the FCM token to the clipboard
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Device Push Token", pushtoken)
        clipboard.setPrimaryClip(clip)

        // Show a Toast message
        Toast.makeText(this, "Device Push Token copied!", Toast.LENGTH_SHORT).show()

    }


    private fun copyDeviceGuid() {
        // Get the GUID
        val guid = Smartech.getInstance(WeakReference(applicationContext)).getDeviceUniqueId()

        // Copy the GUID to the clipboard
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Device GUID", guid)
        clipboard.setPrimaryClip(clip)

        // Show a Toast message
        Toast.makeText(this, "Device GUID copied!", Toast.LENGTH_SHORT).show()


    }


    private fun trackAddToWishList() {
        Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
        val payload : HashMap<String, Any> = HashMap()
        payload["test"] = "5"
        payload["hello"] = "harish"
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("add_to_whishlist", payload)
    }
    // Custom events tracking

    private fun trackAddToCart() {
        Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show()

        val payload : HashMap<String, Any> = HashMap()
        payload["test"] = "5"
        payload["hello"] = "harish"
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("add_to_cart",payload)
    }

   // Custom events tracking

    private fun trackCheckout() {
        val payload : HashMap<String, Any> = HashMap()
            payload["drawdown-flexyreqid"] = "FLEXI9098888 5"
            payload["drawdown_flexyreqid"] = "flexi9098888"
        Smartech.getInstance(WeakReference(applicationContext)).trackEvent("checkout", payload)
        Toast.makeText(this, "Proceeding to Checkout!", Toast.LENGTH_SHORT).show()
    }


   // user profile update activity
    private fun updateProfile() {
        startActivity(Intent(this, UpdateProfileScreen::class.java))
    }

    //clear use identity from app
    private fun clearIdentity() {
        Toast.makeText(this, "User identity cleared!", Toast.LENGTH_SHORT).show()
        Smartech.getInstance(WeakReference(applicationContext)).clearUserIdentity()
    }
    //clear use identity for Smartech and hansel db
   /* private fun logoutUser() {

        val editor = preferences.edit()
        editor.remove(LoginScreen.KEY_EMAIL)
        editor.remove(LoginScreen.KEY_PASSWORD)
        editor.remove(LoginScreen.KEY_CHECKBOX)
        editor.apply()

        // Clear tracking identities
        Smartech.getInstance(WeakReference(this)).logoutAndClearUserIdentity(true)
        Hansel.getUser().clear()

        // Show a Toast message
        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show()

        // Navigate to the login screen
        val loginIntent = Intent(this, LoginScreen::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(loginIntent)
        finish()


    }*/

    private fun logoutUser() {
        val editor = getSharedPreferences(LoginScreen.SHARED_PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()

        Smartech.getInstance(WeakReference(applicationContext)).logoutAndClearUserIdentity(true)
        Hansel.getUser().clear()

        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show()

        val loginIntent = Intent(this, LoginScreen::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(loginIntent)
        finish()
    }



    // default Ui Appinbox code

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
        //double push notification option
        SmartPush.getInstance(WeakReference(this)).resetNotificationDoubleOptIn()
    }


    //Custom Ui appinbox
    private fun openCustomAppInbox() {
        Toast.makeText(this, "Opening Custom App Inbox!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, CustomInbox::class.java))
    }


    // opt in and opt out for push notification and custom events and inapp's
    private fun initializeSwitches() {
        // Initialize SmartPush and Smartech instances
        val smartPushInstance = SmartPush.getInstance(WeakReference(this))
        val smartechInstance = Smartech.getInstance(WeakReference(this))

        // Set initial states for switches
        switchPushNotifications.isChecked = smartPushInstance.hasOptedPushNotification()
        switchInAppMessages.isChecked = smartechInstance.hasOptedInAppMessage()
        switchTracking.isChecked = smartechInstance.hasOptedTracking()

        // Listener for Push Notifications Switch
        switchPushNotifications.setOnCheckedChangeListener { _, isChecked ->
            smartPushInstance.optPushNotification(isChecked)
        }

        // Listener for In-App Messages Switch
        switchInAppMessages.setOnCheckedChangeListener { _, isChecked ->
            smartechInstance.optInAppMessage(isChecked)
        }

        // Listener for Tracking Switch
        switchTracking.setOnCheckedChangeListener { _, isChecked ->
            smartechInstance.optTracking(isChecked)
        }
    }



// Location permissons and Track lat and longitude
    private fun setLocation() {
        if (checkPermissions()) {
            getCurrentLocation()
        } else {
            requestPermissions()
        }
    }

    private fun getCurrentLocation() {
        // Check if fine location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions() // Request permissions if not granted
            return
        }

        // Check if background location permission is granted (for Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestBackgroundPermission() // Request background location permission
            return
        }

        // Retrieve the last known location
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Pass the location to Smartech
                    Smartech.getInstance(WeakReference(applicationContext))
                        .setUserLocation(location)

                    // Inform user about successful location retrieval
                    Toast.makeText(this, "Location set: $latitude, $longitude", Toast.LENGTH_SHORT).show()
                } else {
                    // Handle null location scenario
                    Toast.makeText(this, "Location unavailable. Ensure GPS is enabled.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors while retrieving the location
                Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestPermissions() {
        // Request both fine location and background location permissions (for Android 10+)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestBackgroundPermission() {
        // Specifically request background location permission for Android 10+
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        // Check if fine location and background location permissions are granted
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Background location permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



































































/*import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import java.lang.ref.WeakReference*/


/*
class DashBoardScreen : AppCompatActivity() {
    // Define your views
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
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_screen)



        // Initialize all views
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

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set click listeners using lambdas
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


    }

    // Example method implementations
    private fun copyFcmToken() {

        Toast.makeText(this, "FCM Token copied!", Toast.LENGTH_SHORT).show()
        // You can also implement clipboard functionality here
    }

    private fun copyDeviceGuid() {


        Toast.makeText(this, "Device GUID copied!", Toast.LENGTH_SHORT).show()
        // Similar to above, clipboard functionality can be added
    }

    private fun trackAddToWishList() {
        // Track add to wish list event
        Toast.makeText(this, "Added to Wishlist!", Toast.LENGTH_SHORT).show()
    }

    private fun trackAddToCart() {
        // Track add to cart event
        Toast.makeText(this, "Added to Cart!", Toast.LENGTH_SHORT).show()
    }

    private fun trackCheckout() {
        // Track checkout event
        Toast.makeText(this, "Proceeding to Checkout!", Toast.LENGTH_SHORT).show()
        val payload : HashMap<String, Any> = HashMap()
        payload["drawdown-flexyreqid"] = "FLEXI9098888"
        payload["drawdown_flexyreqid"] = "flexi9098888"


        Smartech.getInstance(WeakReference(applicationContext)).trackEvent( "check out", payload)
    }

    private fun updateProfile() {
        // Code to update profile
        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()


    }

    private fun clearIdentity() {
        // Code to clear user identity
        Toast.makeText(this, "User identity cleared!", Toast.LENGTH_SHORT).show()
    }

    private fun logoutUser() {
        // Handle user logout
        Toast.makeText(this, "User Logged Out!", Toast.LENGTH_SHORT).show()

        Toast.makeText(applicationContext, "Logging out...", Toast.LENGTH_SHORT).show()

        val editor = preferences.edit()
        editor.clear() // Clear preferences on logout
        editor.apply()
        // Implement logout logic, like clearing session data
        Smartech.getInstance(WeakReference(this)).logoutAndClearUserIdentity(true)

    }

    private fun openAppInbox() {
        SmartechAppInbox.getInstance(WeakReference(applicationContext)).displayAppInbox(this)
    }

    private fun openCustomAppInbox() {
        // Open the Custom App Inbox
        Toast.makeText(this, "Opening Custom App Inbox!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, CustomInbox::class.java) // Navigate to your CustomAppInboxActivity
        startActivity(intent)
    }

    private fun setLocation() {
        // Handle location setting
        Toast.makeText(this, "Setting location!", Toast.LENGTH_SHORT).show()


    }




    }
*/




