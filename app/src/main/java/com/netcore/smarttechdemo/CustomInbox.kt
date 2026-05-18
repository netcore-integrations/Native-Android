package com.netcore.smarttechdemo

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.MaterialToolbar
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechappinbox.network.listeners.SMTInboxCallback
import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData
import com.netcore.android.smartechappinbox.utility.SMTAppInboxMessageType
import com.netcore.android.smartechappinbox.utility.SMTAppInboxRequestBuilder
import com.netcore.android.smartechappinbox.utility.SMTInboxDataType
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CustomInbox : AppCompatActivity() {

    private lateinit var toolbar       : MaterialToolbar
    private lateinit var tvBadgeCount  : TextView
    private lateinit var bellContainer : View
    private lateinit var progressBar   : ProgressBar
    private lateinit var swipeRefresh  : SwipeRefreshLayout
    private lateinit var recyclerView  : RecyclerView
    private lateinit var tvSwipeHint   : TextView
    private lateinit var emptyState    : LinearLayout

    private lateinit var inbox : SmartechAppInbox
    private var adapter        : InboxAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_inbox)

        inbox = SmartechAppInbox.getInstance(WeakReference(applicationContext))

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        applyWindowInsets()
        updateBadge()
        fetchInboxMessages(isRefresh = false)
    }

    private fun initViews() {
        toolbar       = findViewById(R.id.toolbar)
        tvBadgeCount  = findViewById(R.id.tv_badge_count)
        bellContainer = findViewById(R.id.bell_container)
        progressBar   = findViewById(R.id.progressBar)
        swipeRefresh  = findViewById(R.id.swipeRefresh)
        recyclerView  = findViewById(R.id.recyclerView)
        tvSwipeHint   = findViewById(R.id.tv_swipe_hint)
        emptyState    = findViewById(R.id.empty_state)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Inbox"
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        bellContainer.setOnClickListener {
            val count = inbox.getAppInboxMessageCount(SMTAppInboxMessageType.UNREAD_MESSAGE)
            Toast.makeText(this, "$count unread message${if (count != 1) "s" else ""}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private val background = ColorDrawable(ContextCompat.getColor(this@CustomInbox, R.color.inbox_swipe_delete))
            private val icon = ContextCompat.getDrawable(this@CustomInbox, android.R.drawable.ic_menu_delete)

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter?.dismissItem(viewHolder.adapterPosition)
                updateBadge()
                showEmptyStateIfNeeded()
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
                actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                background.setBounds(
                    itemView.right + dX.toInt(), itemView.top,
                    itemView.right, itemView.bottom
                )
                background.draw(c)

                icon?.let {
                    val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                    val iconTop    = itemView.top + iconMargin
                    val iconLeft   = itemView.right - iconMargin - it.intrinsicWidth
                    it.setBounds(iconLeft, iconTop, iconLeft + it.intrinsicWidth, iconTop + it.intrinsicHeight)
                    it.setTint(Color.WHITE)
                    it.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.inbox_accent)
        )
        swipeRefresh.setOnRefreshListener {
            fetchInboxMessages(isRefresh = true)
        }
    }

    private fun applyWindowInsets() {
        val basePx = (16 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(recyclerView) { view, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft, view.paddingTop, view.paddingRight, bars.bottom + basePx)
            windowInsets
        }
    }

    private fun updateBadge() {
        val count = inbox.getAppInboxMessageCount(SMTAppInboxMessageType.UNREAD_MESSAGE)
        if (count > 0) {
            tvBadgeCount.text       = if (count > 99) "99+" else count.toString()
            tvBadgeCount.visibility = View.VISIBLE
        } else {
            tvBadgeCount.visibility = View.GONE
        }
    }

    private fun showEmptyStateIfNeeded() {
        val isEmpty = adapter == null || adapter!!.itemCount == 0
        emptyState.visibility   = if (isEmpty) View.VISIBLE else View.GONE
        swipeRefresh.visibility = if (isEmpty) View.GONE    else View.VISIBLE
        tvSwipeHint.visibility  = if (isEmpty) View.GONE    else View.VISIBLE
    }

    private fun fetchInboxMessages(isRefresh: Boolean) {
        val categoryList = inbox.getAppInboxCategoryList().map { it.name }.toMutableList()

        val request = SMTAppInboxRequestBuilder.Builder(SMTInboxDataType.ALL)
            .setCallback(object : SMTInboxCallback {

                override fun onInboxProgress() {
                    runOnUiThread {
                        if (!isRefresh) progressBar.visibility = View.VISIBLE
                    }
                }

                override fun onInboxFail() {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                        Toast.makeText(applicationContext, "Failed to load inbox.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onInboxSuccess(data: MutableList<SMTInboxMessageData>?) {
                    Log.i("INBOX", "Messages: $data")
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false

                        if (data.isNullOrEmpty()) {
                            showEmptyStateIfNeeded()
                            return@runOnUiThread
                        }

                        val messages = data.map { it.toInboxMessage() }.toMutableList()

                        adapter = InboxAdapter(
                            messages           = messages,
                            inbox              = inbox,
                            onMessageDismissed = ::updateBadge
                        )
                        recyclerView.adapter = adapter

                        swipeRefresh.visibility = View.VISIBLE
                        tvSwipeHint.visibility  = View.VISIBLE
                        emptyState.visibility   = View.GONE
                        updateBadge()
                    }
                }
            })
            .setCategory(categoryList)
            .setLimit(50)
            .build()

        inbox.getAppInboxMessages(request)
    }

    private fun SMTInboxMessageData.toInboxMessage(): InboxMessage {
        val p = smtPayload
        val description = when {
            !p.subTitle.isNullOrBlank() -> p.subTitle
            !p.body.isNullOrBlank()     -> p.body
            else                        -> ""
        }
        val isRead = p.status?.equals("DELIVERED", ignoreCase = true) == false
        return InboxMessage(
            title       = p.title       ?: "",
            description = description,
            time        = utcToIst(p.publishedDate),
            mediaUrl    = p.mediaUrl    ?: "",
            deeplink    = p.deeplink,
            isRead      = isRead,
            rawData     = this
        )
    }

    private fun utcToIst(utcString: String?): String {
        if (utcString.isNullOrBlank()) return ""
        return try {
            val utcFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            utcFmt.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFmt.parse(utcString) ?: return utcString

            val istFmt = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            istFmt.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            istFmt.format(date)
        } catch (e: Exception) {
            utcString
        }
    }
}
