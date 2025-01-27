package com.netcore.smarttechdemo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData
import java.lang.ref.WeakReference
class InboxAdapter(private val messages: List<InboxMessage>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    // ViewHolder class holds references to the views for each item in the RecyclerView
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textview)           // Title of the message
        val body: TextView = itemView.findViewById(R.id.textview2)          // Body/content of the message
        val time: TextView = itemView.findViewById(R.id.textview3)          // Timestamp of the message
            // Optional subtitle or divider
        val imageView: ImageView = itemView.findViewById(R.id.imageview)    // Image associated with the message
    }

    // Called when RecyclerView needs a new ViewHolder to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inbox_message, parent, false)            // Inflate the layout for each item
        return ViewHolder(view)                                             // Return the ViewHolder with the inflated layout
    }

    // Binds data to the ViewHolder based on the position of the item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context                                // Context for Glide and SDK calls
        val smartechAppInbox = SmartechAppInbox.getInstance(WeakReference(context))

        val message = messages[position]                                    // Get the current message based on position

        // Bind message data to views
        holder.title.text = message.title                                   // Set title
        holder.body.text = message.body                                     // Set body
        holder.time.text = message.time




        // Load image using Glide or set placeholder if no URL is available
        if (!message.mediaUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(message.mediaUrl)                                     // Load image URL
                .placeholder(R.drawable.ic_launcher_round)                 // Placeholder image while loading
                .into(holder.imageView)                                     // Set image in ImageView
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_round) // Default placeholder image
        }

        // Mark the message as viewed in the Smartech SDK
        smartechAppInbox.markMessageAsViewed(message.toSMTInboxMessageData())

        // Set up click listener for the item
        holder.itemView.setOnClickListener {
            try {
                // Mark the message as clicked in the Smartech SDK
                smartechAppInbox.markMessageAsClicked(
                    message.deeplink.toString(),
                    message.toSMTInboxMessageData()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to mark message as clicked", Toast.LENGTH_SHORT).show()
            }

            try {
                // Navigate to the deeplink if it's valid
                if (!message.deeplink.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.deeplink))
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "No Deeplink Available", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Invalid Deeplink", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the total number of items in the dataset
    override fun getItemCount(): Int = messages.size
}











































































































































































































/*/*
class InboxAdapter(private val messages: List<InboxMessage>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textview)
        val body: TextView = itemView.findViewById(R.id.textview2)
        val time: TextView = itemView.findViewById(R.id.textview3)
        val subtitle: TextView = itemView.findViewById(R.id.Divider)
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inbox_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val smartechAppInbox = SmartechAppInbox.getInstance(WeakReference(context))

        val message = messages[position]

        // Set the title, body, and time fields
        holder.title.text = message.title
        holder.body.text = message.body
        holder.time.text = message.time


        // Load image using Glide
        if (!message.mediaUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(message.mediaUrl) // Load the image from the URL
                .placeholder(R.drawable.ic_launcher_round) // Show placeholder while loading
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_round) // Show placeholder if no image URL
        }

        // App inbox viewed
        smartechAppInbox.markMessageAsViewed(message.toSMTInboxMessageData())

        // Uncomment and modify if deeplink handling is needed
      holder.itemView.setOnClickListener {
          // App inbox viewed mark the message as clicked
          try {
              smartechAppInbox.markMessageAsClicked(message.deeplink.toString(),message.toSMTInboxMessageData()) // Convert to SMTInboxMessageData
          } catch (e: Exception) {
              e.printStackTrace()
              Toast.makeText(context, "Failed to mark message as clicked", Toast.LENGTH_SHORT).show()
          }

          // Handle deep link navigation
          try {
              if (!message.deeplink.isNullOrEmpty()) {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.deeplink))
                  context.startActivity(intent)
              } else {
                  Toast.makeText(context, "No Deeplink Available", Toast.LENGTH_SHORT).show()
              }
          } catch (e: Exception) {
              e.printStackTrace()
              Toast.makeText(context, "Invalid Deeplink", Toast.LENGTH_SHORT).show()
          }
        }
    }

    override fun getItemCount(): Int = messages.size
}
*/

class InboxAdapter(private val messages: List<InboxMessage>) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textview)
        val body: TextView = itemView.findViewById(R.id.textview2)
        val time: TextView = itemView.findViewById(R.id.textview3)
        val subtitle: TextView = itemView.findViewById(R.id.Divider)
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inbox_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val smartechAppInbox = SmartechAppInbox.getInstance(WeakReference(context))

        val message = messages[position]

        // Set the title, body, and time fields
        holder.title.text = message.title
        holder.body.text = message.body
        holder.time.text = message.time


        // Load image using Glide
        if (!message.mediaUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(message.mediaUrl) // Load the image from the URL
                .placeholder(R.drawable.ic_launcher_round) // Show placeholder while loading
                .into(holder.imageView)
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_round) // Show placeholder if no image URL
        }

        // App inbox viewed
        smartechAppInbox.markMessageAsViewed(message.toSMTInboxMessageData())

        // Uncomment and modify if deeplink handling is needed
      holder.itemView.setOnClickListener {
          // App inbox viewed mark the message as clicked
          try {
              smartechAppInbox.markMessageAsClicked(message.deeplink.toString(),message.toSMTInboxMessageData()) // Convert to SMTInboxMessageData
          } catch (e: Exception) {
              e.printStackTrace()
              Toast.makeText(context, "Failed to mark message as clicked", Toast.LENGTH_SHORT).show()
          }

          // Handle deep link navigation
          try {
              if (!message.deeplink.isNullOrEmpty()) {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(message.deeplink))
                  context.startActivity(intent)
              } else {
                  Toast.makeText(context, "No Deeplink Available", Toast.LENGTH_SHORT).show()
              }
          } catch (e: Exception) {
              e.printStackTrace()
              Toast.makeText(context, "Invalid Deeplink", Toast.LENGTH_SHORT).show()
          }
        }
    }

    override fun getItemCount(): Int = messages.size
}
*/


