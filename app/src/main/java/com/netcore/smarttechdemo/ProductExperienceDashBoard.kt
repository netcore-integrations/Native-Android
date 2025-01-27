package com.netcore.smarttechdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.netcore.android.Smartech
import io.hansel.hanselsdk.Hansel
import io.hansel.hanselsdk.HanselDeepLinkListener
import io.hansel.ujmtracker.HanselInternalEventsListener
import io.hansel.ujmtracker.HanselTracker
import java.lang.ref.WeakReference

class ProductExperienceDashBoard : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_product_experience)

        val ivCustomizeSettings = findViewById<ImageView>(R.id.iv_customize_settings)
        val ivExploreFeatures = findViewById<ImageView>(R.id.iv_explore_features)
        val ivFeedback = findViewById<ImageView>(R.id.iv_feedback)
        val dynamicView = findViewById<CardView>(R.id.dynamic_view)

        // Handle over lapping code for invisible container
        ivCustomizeSettings.setOnClickListener {
            startActivity(Intent(this, HanselIgnoreView::class.java))
        }

        // Handle Explore Features click
        ivExploreFeatures.setOnClickListener {
            Toast.makeText(this, "Exploring features!", Toast.LENGTH_SHORT).show()

           // hanselActionListener()
          //  hanselEventListener()
        }


        // Create an instance of HanselDeepLinkListener
        val hanselActionsListener = object : HanselDeepLinkListener {
            override fun onLaunchUrl(url: String?) {
  Toast.makeText(this@ProductExperienceDashBoard, "Deeplink Available", Toast.LENGTH_SHORT).show()

                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                    }
                    startActivity(intent)
                } else {
 Toast.makeText(this@ProductExperienceDashBoard, "Deeplink Not Available", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Register the instance
        Hansel.registerHanselDeeplinkListener(hanselActionsListener)





        val hanselInternalEventsListener = HanselInternalEventsListener { eventName, dataFromHansel ->
            Smartech.getInstance(WeakReference(applicationContext))
                .trackEvent(eventName, dataFromHansel as HashMap<String, Any>?)
        }
        HanselTracker.registerListener(hanselInternalEventsListener)









        dynamicView.setOnClickListener{
            startActivity(Intent(this, Dynamicview::class.java))
        }




        // Handle feed back of code
        ivFeedback.setOnClickListener {
            val feedbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("harish.reddy@netcorecloud.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Feedback on Product Experience and Android Integrations")
            }
            startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"))
        }

    }


    // Hansel event lister code for sending events data to analytics

    private fun hanselEventListener() {
        val hanselInternalEventsListener = HanselInternalEventsListener { eventName, dataFromHansel ->
            Smartech.getInstance(WeakReference(applicationContext))
                .trackEvent(eventName, dataFromHansel as HashMap<String, Any>?)
        }
        HanselTracker.registerListener(hanselInternalEventsListener)
    }


    // hansel action listner for nudge button action.
    private fun hanselActionListener() {
        val hanselActionsListener = object : HanselDeepLinkListener {
            override fun onLaunchUrl(url: String?) {
                if (!url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    this@ProductExperienceDashBoard.startActivity(intent)
                } else {
                    Toast.makeText(this@ProductExperienceDashBoard, "Deeplink Not Available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        Hansel.registerHanselDeeplinkListener(hanselActionsListener)
    }
}
