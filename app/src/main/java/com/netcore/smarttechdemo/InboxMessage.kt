package com.netcore.smarttechdemo

import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData

data class InboxMessage(
    val title: String,
    val body: String,
    val time: String,
    val mediaUrl:String,
    val deeplink: String?
) {
    fun toSMTInboxMessageData(): SMTInboxMessageData {
        // Replace with actual mapping logic based on the Smartech SDK
        return SMTInboxMessageData(/* initialize with appropriate data */)
    }
}

