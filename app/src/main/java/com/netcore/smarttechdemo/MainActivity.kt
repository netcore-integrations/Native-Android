package com.netcore.smarttechdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var btnCe: MaterialCardView
    private lateinit var btnPx: MaterialCardView
    private lateinit var vpBanners: ViewPager2
    private lateinit var llDots: LinearLayout

    // Auto-scroll banner
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var currentBannerPage = 0

    private val banners = listOf(
        BannerItem(
            tag = "BIRTHDAY SPECIAL",
            title = "Happy Birthday\nfrom Boost!",
            subtitle = "Here is our special gift just for you",
            ctaText = "CLAIM GIFT",
            bgDrawable = R.drawable.bg_banner_slide_1
        ),
        BannerItem(
            tag = "NEW FEATURE",
            title = "Dynamic Indexing\nNow Live!",
            subtitle = "Personalize every screen with Hansel nudges",
            ctaText = "EXPLORE NOW",
            bgDrawable = R.drawable.bg_banner_slide_2
        ),
        BannerItem(
            tag = "PROMO",
            title = "Push Notifications\nDone Right",
            subtitle = "Double opt-in, channels and geofence ready",
            ctaText = "LEARN MORE",
            bgDrawable = R.drawable.bg_banner_slide_3
        )
    )

    private val notificationPermissionCallback = object : SMTNotificationPermissionCallback {
        override fun notificationPermissionStatus(status: Int) {
            // Status handled by SDK internally
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext))
            .updateNotificationPermission()

        initUI()
        setupBannerCarousel()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        startBannerAutoScroll()
    }

    override fun onPause() {
        super.onPause()
        bannerHandler.removeCallbacksAndMessages(null)
    }

    // ── UI init ───────────────────────────────────────────────────────────
    private fun initUI() {
        btnCe = findViewById(R.id.btn_ce)
        btnPx = findViewById(R.id.btn_px)
        vpBanners = findViewById(R.id.vp_banners)
        llDots = findViewById(R.id.ll_dots)

        // Quick links
        findViewById<MaterialCardView>(R.id.btn_dynamic_view)?.setOnClickListener {
            startActivity(Intent(this, Dynamicview::class.java))
        }
        findViewById<MaterialCardView>(R.id.btn_quick_inbox)?.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
        }
        findViewById<ImageView>(R.id.btn_profile_header)?.setOnClickListener {
            startActivity(Intent(this, UpdateProfileScreen::class.java))
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────
    private fun setupNavigation() {
        btnCe.setOnClickListener {
            startActivity(Intent(this, DashBoardScreen::class.java))
        }
        btnPx.setOnClickListener {
            startActivity(Intent(this, ProductExperienceDashBoard::class.java))
        }
    }

    // ── Banner carousel ───────────────────────────────────────────────────
    private fun setupBannerCarousel() {
        val adapter = BannerAdapter(banners) { item ->
            Toast.makeText(this, "${item.ctaText} tapped", Toast.LENGTH_SHORT).show()
        }
        vpBanners.adapter = adapter
        vpBanners.offscreenPageLimit = 1

        // Build initial dots
        buildDots(banners.size, 0)

        // Update dots on page change
        vpBanners.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentBannerPage = position
                buildDots(banners.size, position)
                // Restart auto-scroll after manual swipe
                bannerHandler.removeCallbacksAndMessages(null)
                startBannerAutoScroll()
            }
        })
    }

    // ── Dots: pill shape for active, circle for inactive ─────────────────
    private fun buildDots(count: Int, selected: Int) {
        llDots.removeAllViews()
        val density = resources.displayMetrics.density
        val dotH = (6 * density).toInt()
        val activeW = (20 * density).toInt()   // pill
        val inactiveW = (6 * density).toInt()  // circle
        val margin = (4 * density).toInt()

        for (i in 0 until count) {
            val dot = View(this)
            val lp = LinearLayout.LayoutParams(
                if (i == selected) activeW else inactiveW,
                dotH
            )
            lp.setMargins(margin, 0, margin, 0)
            dot.layoutParams = lp
            dot.background = ContextCompat.getDrawable(
                this,
                if (i == selected) R.drawable.dot_active else R.drawable.dot_inactive
            )
            llDots.addView(dot)
        }
    }

    // ── Auto-scroll every 3.5 s ───────────────────────────────────────────
    private fun startBannerAutoScroll() {
        bannerHandler.postDelayed(object : Runnable {
            override fun run() {
                if (banners.isNotEmpty()) {
                    currentBannerPage = (currentBannerPage + 1) % banners.size
                    vpBanners.setCurrentItem(currentBannerPage, true)
                }
                bannerHandler.postDelayed(this, 3500L)
            }
        }, 3500L)
    }
}
