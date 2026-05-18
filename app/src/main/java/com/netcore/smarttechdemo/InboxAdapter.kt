package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.netcore.android.smartechappinbox.SmartechAppInbox

class InboxAdapter(
    private val messages: MutableList<InboxMessage>,
    private val inbox: SmartechAppInbox,
    private val onMessageDismissed: () -> Unit
) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    private val viewedTrids = HashSet<String>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRoot   : MaterialCardView = itemView.findViewById(R.id.card_root)
        val accentBar  : View      = itemView.findViewById(R.id.accent_bar)
        val title      : TextView  = itemView.findViewById(R.id.textview)
        val description: TextView  = itemView.findViewById(R.id.textview2)
        val time       : TextView  = itemView.findViewById(R.id.textview3)
        val image      : ImageView = itemView.findViewById(R.id.imageview)
        val unreadDot  : View      = itemView.findViewById(R.id.unread_dot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_inbox_message, parent, false)
        )

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg     = messages[position]
        val context = holder.itemView.context
        val trid    = msg.rawData.smtPayload?.trid ?: ""

        holder.title.text       = if (msg.title.isNotBlank())       inbox.parseHtml(msg.title)       else ""
        holder.description.text = if (msg.description.isNotBlank()) inbox.parseHtml(msg.description) else ""
        holder.time.text        = msg.time

        val isUnread = !msg.isRead && trid !in viewedTrids
        applyReadState(holder, isUnread, context)

        if (msg.mediaUrl.isNotBlank()) {
            Glide.with(context)
                .load(msg.mediaUrl)
                .placeholder(R.drawable.ic_launcher_round)
                .error(R.drawable.ic_launcher_round)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_round)
        }

        if (trid.isNotEmpty() && trid !in viewedTrids) {
            viewedTrids.add(trid)
            inbox.markMessageAsViewed(msg.rawData)
            applyReadState(holder, isUnread = false, context)
        }

        holder.itemView.setOnClickListener {
            inbox.markMessageAsClicked(msg.deeplink ?: "", msg.rawData)
            openUrl(context, msg.deeplink)
        }
    }

    private fun applyReadState(holder: ViewHolder, isUnread: Boolean, context: android.content.Context) {
        if (isUnread) {
            holder.cardRoot.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.inbox_unread_bg)
            )
            holder.accentBar.visibility = View.VISIBLE
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.inbox_title_unread))
            holder.unreadDot.visibility = View.VISIBLE
        } else {
            holder.cardRoot.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.inbox_read_bg)
            )
            holder.accentBar.visibility = View.GONE
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.inbox_title_read))
            holder.unreadDot.visibility = View.GONE
        }
    }

    fun dismissItem(position: Int) {
        if (position < 0 || position >= messages.size) return
        val msg = messages[position]
        inbox.markMessageAsDismissed(msg.rawData)
        messages.removeAt(position)
        notifyItemRemoved(position)
        onMessageDismissed()
    }

    private fun openUrl(context: android.content.Context, url: String?) {
        if (url.isNullOrBlank()) return
        try {
            val uri    = Uri.parse(url)
            val intent = when {
                uri.scheme?.startsWith("http", ignoreCase = true) == true ->
                    Intent(Intent.ACTION_VIEW, uri).apply {
                        addCategory(Intent.CATEGORY_BROWSABLE)
                    }
                else -> Intent(Intent.ACTION_VIEW, uri)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }
}
