package com.netcore.smarttechdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechappinbox.network.listeners.SMTInboxCallback
import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData
import com.netcore.android.smartechappinbox.utility.SMTAppInboxRequestBuilder
import com.netcore.android.smartechappinbox.utility.SMTInboxDataType
import java.lang.ref.WeakReference


class CustomInbox : AppCompatActivity() {

    // Declare UI components and context
    private lateinit var btnAppInbox: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var context: Context



    // onCreate method initializes the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_inbox)

        supportActionBar?.hide()



        initUI() // Initialize UI components

        // Set click listener for the APPINBOX button
        btnAppInbox.setOnClickListener {
            fetchInboxMessages() // Fetch messages on button click
        }
    }

    // Fetch inbox messages from Smartech
    private fun fetchInboxMessages() {
        val smartechAppInbox = SmartechAppInbox.getInstance(WeakReference(applicationContext))
        val categoryList = mutableListOf<String>()
        for (category in smartechAppInbox.getAppInboxCategoryList()) {
            categoryList.add(category.name)
        }



        //val categoryList = arrayListOf("sidiksha") // Define category filter
        Toast.makeText(applicationContext, "Fetching inbox messages...", Toast.LENGTH_SHORT).show()



        /*// Retrieve and log messages of type READ_MESSAGE
        val readMessages = smartechAppInbox.getAppInboxMessages(SMTAppInboxMessageType.READ_MESSAGE)
        Log.i("INBOX Read Messages", " ReadMessages fetched: ${readMessages.toString()}")

        val unreadMessages = smartechAppInbox.getAppInboxMessages(SMTAppInboxMessageType.UNREAD_MESSAGE)
        Log.i("INBOX UnRead Messages", " UnReadMessages fetched: ${unreadMessages.toString()}")

        val messages = smartechAppInbox.getAppInboxMessages(SMTAppInboxMessageType.INBOX_MESSAGE)
        Log.i("INBOX Messages", "INBOX_MESSAGE: ${messages.toString()}")
*/

        // Build the inbox request
        val builder = SMTAppInboxRequestBuilder.Builder(SMTInboxDataType.ALL)
            .setCallback(object : SMTInboxCallback {

                override fun onInboxFail() {
                    hideProgressBar() // Hide progress bar on failure
                    Toast.makeText(applicationContext, "Failed to fetch inbox messages.", Toast.LENGTH_SHORT).show()
                }

                override fun onInboxProgress() {
                    runOnUiThread {
                        progressBar.visibility = View.VISIBLE // Show progress bar while loading
                    }
                }


override fun onInboxSuccess(messages: MutableList<SMTInboxMessageData>?) {

    Log.i("INBOX Success", "Messages fetched: ${messages.toString()}")
    runOnUiThread {
        hideProgressBar() // Hide progress bar after success

        if (!messages.isNullOrEmpty()) {
            // Map SMTInboxMessageData to InboxMessage for RecyclerView
            val inboxMessages = messages.map { messageData ->
                InboxMessage(
                    title = messageData.smtPayload.title ?: "No Title",
                    body = messageData.smtPayload.body ?: "No Body",
                    time = messageData.smtPayload.publishedDate ?: "No Date",
                    mediaUrl = messageData.smtPayload.mediaUrl ?: "",
                    deeplink = messageData.smtPayload.deeplink
                )
            }

            // Update RecyclerView with fetched messages
            val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@CustomInbox)
            recyclerView.adapter = InboxAdapter(inboxMessages)
        } else {
            Toast.makeText(applicationContext, "No messages found.", Toast.LENGTH_SHORT).show()
        }
    }
} })
            .setCategory(categoryList) // Filter messages by category
            .setLimit(10)              // Limit the number of messages
            .build()

        // Execute the inbox request
        smartechAppInbox.getAppInboxMessages(builder)
    }

    // Hide the progress bar
    private fun hideProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.GONE
        }
    }

    // Initialize UI components
    private fun initUI() {
        btnAppInbox = findViewById(R.id.btn_appinbox)      // Button to fetch inbox messages
        progressBar = findViewById(R.id.progressBar)       // Progress bar for loading indicator

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Set layout manager for RecyclerView
    }
}























































































































































































