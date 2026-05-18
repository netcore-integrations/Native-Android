package com.netcore.smarttechdemo

import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData

data class InboxMessage(
    val title: String,
    val description: String,
    val time: String,
    val mediaUrl: String,
    val deeplink: String?,
    val isRead: Boolean,
    val rawData: SMTInboxMessageData
)
