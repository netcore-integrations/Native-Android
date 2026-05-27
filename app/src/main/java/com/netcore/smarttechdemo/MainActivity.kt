package com.netcore.smarttechdemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.netcore.android.Smartech
import com.netcore.android.contentpz.SMTWidgetListener
import com.netcore.android.contentpz.model.SMTWidget
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), SMTWidgetListener {

    companion object {
        private const val TAG          = "MainActivity"
        private const val SCROLL_DELAY = 3500L

        const val WIDGET_HOMEPAGE  = "homepage_carousel"
        const val WIDGET_COMMUNITY = "community_carousel"
        const val WIDGET_BRAND     = "brand_carousel"
        val ALL_WIDGETS = arrayOf(WIDGET_HOMEPAGE, WIDGET_COMMUNITY, WIDGET_BRAND)
    }

    private lateinit var btnCe          : MaterialCardView
    private lateinit var btnPx          : MaterialCardView
    private lateinit var vpBanners      : ViewPager2
    private lateinit var llDots         : LinearLayout
    private lateinit var tvSectionLabel : TextView
    private lateinit var tvBannerMode   : TextView
    private lateinit var carouselAdapter: CarouselPagerAdapter

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var currentPage   = 0
    private val loadedWidgets = HashMap<String, SMTWidget?>()

    private val staticBanners: List<CarouselBannerItem> = listOf(
        CarouselBannerItem(
            title            = "Welcome to Smartech Demo",
            message          = "Explore personalised campaigns, push, and in-app features.",
            mediaUrl         = "",
            deeplinkUrl      = "",
            backgroundColor  = "#CC0000",
            actionButtons    = listOf(CarouselActionButton("Explore", "", "#FFFFFF", "#CC0000")),
            localDrawableRes = R.drawable.bg_banner_slide_1
        ),
        CarouselBannerItem(
            title            = "Dynamic Indexing Now Live!",
            message          = "Personalise every screen with Hansel nudges.",
            mediaUrl         = "",
            deeplinkUrl      = "sampleapp://dashboard",
            backgroundColor  = "#1565C0",
            actionButtons    = listOf(CarouselActionButton("Explore Now", "sampleapp://dashboard", "#FFFFFF", "#1565C0")),
            localDrawableRes = R.drawable.bg_banner_slide_2
        ),
        CarouselBannerItem(
            title            = "Push Notifications Done Right",
            message          = "Double opt-in, channels and geofence ready.",
            mediaUrl         = "",
            deeplinkUrl      = "",
            backgroundColor  = "#2E7D32",
            actionButtons    = listOf(CarouselActionButton("Learn More", "", "#FFFFFF", "#2E7D32")),
            localDrawableRes = R.drawable.bg_banner_slide_3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(object : SMTNotificationPermissionCallback {
                override fun notificationPermissionStatus(status: Int) {}
            })
        SmartPush.getInstance(WeakReference(applicationContext)).updateNotificationPermission()

        initUI()
        setupNavigation()
        setupBannerCarousel(staticBanners)

        val sdk = Smartech.getInstance(WeakReference(this))
        Log.i(TAG, "onCreate: identity=${sdk.getUserIdentity()}, widgetNames=${sdk.getAllWidgetNames()}")
        sdk.setWidgetListener(this, this)
        sdk.getAllWidgets()
    }

    override fun onResume() {
        super.onResume()
        bannerHandler.removeCallbacksAndMessages(null)
        updateSectionLabel()
        startAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        Smartech.getInstance(WeakReference(this)).removeWidgetListener(this)
    }

    override fun onWidgetsLoaded(data: HashMap<String, SMTWidget?>) {
        Log.i(TAG, "onWidgetsLoaded: size=${data.size}, keys=${data.keys}")

        if (data.isEmpty()) {
            Log.w(TAG, "onWidgetsLoaded: empty — no active campaign targeting this device/identity.")
            return
        }

        val allBanners = mutableListOf<CarouselBannerItem>()

        for ((widgetName, widget) in data) {
            if (widget == null) {
                Log.w(TAG, "  '$widgetName' → null, skipping.")
                continue
            }
            Log.i(TAG, "  '$widgetName': layoutType='${widget.layoutType}', " +
                    "hasContent=${widget.content != null}, " +
                    "title='${widget.content?.title}', " +
                    "mediaUrl='${widget.content?.mediaUrl}', " +
                    "json=${widget.content?.json}")

            val banners = CpzBannerParser.parseFromWidget(widget, widgetName)
            Log.i(TAG, "  '$widgetName' → ${banners.size} banner(s) parsed.")
            if (banners.isEmpty()) continue

            allBanners.addAll(banners)
            loadedWidgets[widgetName] = widget
            Smartech.getInstance(WeakReference(this)).trackWidgetAsViewed(widget)
        }

        if (allBanners.isNotEmpty()) {
            Log.i(TAG, "Swapping carousel with ${allBanners.size} dynamic banner(s).")
            runOnUiThread { swapToDynamicBanners(allBanners) }
        } else {
            Log.w(TAG, "All widgets produced 0 banners — static banners kept.")
        }
    }

    private fun initUI() {
        btnCe          = findViewById(R.id.btn_ce)
        btnPx          = findViewById(R.id.btn_px)
        vpBanners      = findViewById(R.id.vp_banners)
        llDots         = findViewById(R.id.ll_dots)
        tvSectionLabel = findViewById(R.id.tv_section_label)
        tvBannerMode   = findViewById(R.id.tv_banner_mode)

        updateSectionLabel()

        findViewById<ImageView>(R.id.btn_device_info)?.setOnClickListener {
            startActivity(Intent(this, DeviceInfoActivity::class.java))
        }
        findViewById<ImageView>(R.id.btn_profile_header)?.setOnClickListener {
            startActivity(Intent(this, UpdateProfileScreen::class.java))
        }
    }

    private fun updateSectionLabel() {
        val firstName = getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
            .getString("first_name", "").orEmpty().trim()
        tvSectionLabel.text = if (firstName.isNotBlank()) "Featured for $firstName" else "Featured for You"
    }

    private fun setupNavigation() {
        btnCe.setOnClickListener { startActivity(Intent(this, DashBoardScreen::class.java)) }
        btnPx.setOnClickListener { startActivity(Intent(this, ProductExperienceDashBoard::class.java)) }
    }

    private fun setupBannerCarousel(banners: List<CarouselBannerItem>) {
        carouselAdapter = CarouselPagerAdapter(banners.toMutableList()) { onBannerClicked(it) }
        vpBanners.adapter            = carouselAdapter
        vpBanners.offscreenPageLimit = 1
        currentPage = 0
        buildDots(banners.size, 0)
        vpBanners.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
                buildDots(carouselAdapter.itemCount, position)
                bannerHandler.removeCallbacksAndMessages(null)
                startAutoScroll()
            }
        })
    }

    private fun swapToDynamicBanners(dynamic: List<CarouselBannerItem>) {
        bannerHandler.removeCallbacksAndMessages(null)
        currentPage = 0
        carouselAdapter.updateBanners(dynamic)
        buildDots(dynamic.size, 0)
        vpBanners.setCurrentItem(0, false)
        startAutoScroll()

        // Show the widget name received from the panel as the section label
        val rawWidgetName = dynamic.firstOrNull()?.sourceWidgetName.orEmpty()
        tvSectionLabel.text = if (rawWidgetName.isNotBlank()) formatWidgetName(rawWidgetName) else "Featured for You"

        tvBannerMode.text = "LIVE"
        tvBannerMode.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
        tvBannerMode.setTextColor(android.graphics.Color.parseColor("#2E7D32"))
        Log.i(TAG, "Carousel updated with ${dynamic.size} banner(s) from widget '$rawWidgetName'.")
    }

    private fun formatWidgetName(name: String): String =
        name.replace('_', ' ').split(' ')
            .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

    private fun onBannerClicked(banner: CarouselBannerItem) {
        loadedWidgets[banner.sourceWidgetName]?.let {
            Smartech.getInstance(WeakReference(this)).trackWidgetAsClicked(it)
        }
        Smartech.getInstance(WeakReference(this)).trackEvent(
            "cpz_banner_click",
            hashMapOf<String, Any>(
                "widget"   to banner.sourceWidgetName.ifBlank { "static" },
                "title"    to banner.title,
                "deeplink" to banner.deeplinkUrl
            )
        )
    }

    private fun buildDots(count: Int, selected: Int) {
        llDots.removeAllViews()
        val dp      = resources.displayMetrics.density
        val dotH    = (6  * dp).toInt()
        val activeW = (20 * dp).toInt()
        val inactW  = (6  * dp).toInt()
        val margin  = (4  * dp).toInt()
        for (i in 0 until count) {
            val dot = View(this)
            val lp  = LinearLayout.LayoutParams(if (i == selected) activeW else inactW, dotH)
            lp.setMargins(margin, 0, margin, 0)
            dot.layoutParams = lp
            dot.background = ContextCompat.getDrawable(
                this, if (i == selected) R.drawable.dot_active else R.drawable.dot_inactive
            )
            llDots.addView(dot)
        }
    }

    private fun startAutoScroll() {
        bannerHandler.postDelayed(object : Runnable {
            override fun run() {
                val count = carouselAdapter.itemCount
                if (count > 1) {
                    currentPage = (currentPage + 1) % count
                    vpBanners.setCurrentItem(currentPage, true)
                }
                bannerHandler.postDelayed(this, SCROLL_DELAY)
            }
        }, SCROLL_DELAY)
    }
}
