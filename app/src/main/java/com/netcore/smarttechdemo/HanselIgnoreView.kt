package com.netcore.smarttechdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.netcore.android.Smartech
import java.lang.ref.WeakReference

class HanselIgnoreView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hansel_ignore_view)

        // Set the "ignore view" tag for layout1 and all its child views
        findViewById<View>(R.id.layout1).setTag(R.id.hansel_ignore_view, true)

        // Set the "ignore view excluding children" tag for layout2
        findViewById<View>(R.id.layout2).setTag(R.id.hansel_ignore_view_excluding_children, true)

        // Track an event with a payload using Smartech
        val payload = hashMapOf<String, Any>() // HashMap for event payload
        Smartech.getInstance(WeakReference(this)).trackEvent("invisiblecontainer", payload)
    }
}
