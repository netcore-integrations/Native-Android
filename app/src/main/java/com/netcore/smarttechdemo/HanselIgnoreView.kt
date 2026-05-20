package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.netcore.android.Smartech
import com.netcore.smarttechdemo.databinding.HanselIgnoreViewBinding
import io.hansel.hanselsdk.Hansel
import io.hansel.hanselsdk.HanselActionListener
import io.hansel.hanselsdk.HanselDeepLinkListener
import java.lang.ref.WeakReference

class HanselIgnoreView : AppCompatActivity() {

    private lateinit var binding: HanselIgnoreViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HanselIgnoreViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // layout1 and all its children will be ignored during screen capture on Hansel console
        binding.layout1.setTag(R.id.hansel_ignore_view, true)

        // Only layout2 itself is ignored; its children are still captured for nudge placement
        binding.layout2.setTag(R.id.hansel_ignore_view_excluding_children, true)

        Smartech.getInstance(WeakReference(this))
            .trackEvent("invisiblecontainer", HashMap())

        // Register a Hansel action listener for nudge button actions
        Hansel.registerHanselActionListener("Nudge Action", HanselActionListener { action ->
            Toast.makeText(applicationContext, "Nudge action: $action", Toast.LENGTH_SHORT).show()
        })

        // Register a Hansel deeplink listener so nudge deeplinks open in the browser
        Hansel.registerHanselDeeplinkListener(HanselDeepLinkListener { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            startActivity(intent)
        })
    }
}
