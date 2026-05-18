package com.netcore.smarttechdemo

import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.netcore.android.Smartech
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.util.Collections

class Dynamicview : AppCompatActivity() {

    // ── Views ─────────────────────────────────────────────────────────────
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var etSearch: EditText
    private lateinit var btnClearSearch: ImageView
    private lateinit var llCategories: LinearLayout
    private lateinit var tvCountdown: TextView
    private lateinit var pbShuffle: ProgressBar
    private lateinit var btnShuffleNow: TextView
    private lateinit var tvProductCount: TextView
    private lateinit var btnSortPrice: TextView
    private lateinit var btnSortRating: TextView
    private lateinit var tvCartCount: TextView
    private lateinit var flCart: FrameLayout
    private lateinit var btnManualRefresh: ImageView
    private lateinit var llEmptyState: LinearLayout

    // ── Adapter & Data ────────────────────────────────────────────────────
    private lateinit var adapter: DynamicAdapter
    private var allProducts: MutableList<Product> = mutableListOf()
    private var filteredProducts: MutableList<Product> = mutableListOf()

    // ── State ─────────────────────────────────────────────────────────────
    private var selectedCategory = "All"
    private var currentSearchQuery = ""
    private var cartCount = 0
    private var sortMode = SortMode.NONE
    private var countDownTimer: CountDownTimer? = null

    enum class SortMode { NONE, PRICE_ASC, RATING_DESC }

    companion object {
        private const val COUNTDOWN_TOTAL_MS = 15_000L
        private const val COUNTDOWN_INTERVAL_MS = 200L
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamicview)

        bindViews()
        setupRecyclerView()
        setupSearch()
        setupSortButtons()
        setupCartButton()
        setupShuffleButton()
        setupSwipeRefresh()

        fetchProducts()

        // Track page view
        Smartech.getInstance(WeakReference(this))
            .trackEvent("dynamicview_open", hashMapOf())
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // ── View binding ──────────────────────────────────────────────────────
    private fun bindViews() {
        recyclerView = findViewById(R.id.RecyclerView)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        etSearch = findViewById(R.id.et_search)
        btnClearSearch = findViewById(R.id.btn_clear_search)
        llCategories = findViewById(R.id.ll_categories)
        tvCountdown = findViewById(R.id.tv_countdown)
        pbShuffle = findViewById(R.id.pb_shuffle)
        btnShuffleNow = findViewById(R.id.btn_shuffle_now)
        tvProductCount = findViewById(R.id.tv_product_count)
        btnSortPrice = findViewById(R.id.btn_sort_price)
        btnSortRating = findViewById(R.id.btn_sort_rating)
        tvCartCount = findViewById(R.id.tv_cart_count)
        flCart = findViewById(R.id.fl_cart)
        btnManualRefresh = findViewById(R.id.btn_manual_refresh)
        llEmptyState = findViewById(R.id.ll_empty_state)
    }

    // ── RecyclerView / Adapter ─────────────────────────────────────────────
    private fun setupRecyclerView() {
        adapter = DynamicAdapter(
            context = this,
            productList = filteredProducts,
            onAddToCart = { product, _ -> addToCart(product) }
        )
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)
    }

    // ── Retrofit / Data fetch ─────────────────────────────────────────────
    private fun fetchProducts() {
        swipeRefresh.isRefreshing = true

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiInterface::class.java)
            .getProductData()
            .enqueue(object : Callback<MyData?> {
                override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                    swipeRefresh.isRefreshing = false
                    if (response.isSuccessful) {
                        val products = response.body()?.products ?: emptyList()
                        allProducts = products.toMutableList()
                        buildCategoryChips()
                        applyFilters()
                        startCountdownTimer()
                    } else {
                        showError("Failed to load products (${response.code()})")
                    }
                }

                override fun onFailure(call: Call<MyData?>, t: Throwable) {
                    swipeRefresh.isRefreshing = false
                    showError("Network error: ${t.message}")
                }
            })
    }

    // ── Category chips ────────────────────────────────────────────────────
    private fun buildCategoryChips() {
        llCategories.removeAllViews()

        val categories = mutableListOf("All")
        allProducts.map { it.category.replaceFirstChar { c -> c.uppercaseChar() } }
            .distinct()
            .sorted()
            .forEach { categories.add(it) }

        categories.forEach { cat -> llCategories.addView(createChip(cat)) }
    }

    private fun createChip(label: String): TextView {
        val chip = TextView(this)
        chip.text = label
        chip.textSize = 12f
        chip.typeface = Typeface.DEFAULT_BOLD
        chip.gravity = Gravity.CENTER
        chip.setPadding(dp(14), dp(6), dp(14), dp(6))

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(dp(4), 0, dp(4), 0)
        chip.layoutParams = lp

        applyChipStyle(chip, label == selectedCategory)

        chip.setOnClickListener {
            selectedCategory = label
            updateChipStyles()
            applyFilters()
        }
        return chip
    }

    private fun applyChipStyle(chip: TextView, selected: Boolean) {
        if (selected) {
            chip.setBackgroundResource(R.drawable.bg_category_chip_selected)
            chip.setTextColor(getColor(R.color.chip_selected_text))
        } else {
            chip.setBackgroundResource(R.drawable.bg_category_chip)
            chip.setTextColor(getColor(R.color.chip_unselected_text))
        }
    }

    private fun updateChipStyles() {
        for (i in 0 until llCategories.childCount) {
            val chip = llCategories.getChildAt(i) as? TextView ?: continue
            applyChipStyle(chip, chip.text == selectedCategory)
        }
    }

    // ── Filter + Sort ─────────────────────────────────────────────────────
    private fun applyFilters() {
        var result = allProducts.toMutableList()

        // Category filter
        if (selectedCategory != "All") {
            val cat = selectedCategory.lowercase()
            result = result.filter {
                it.category.lowercase() == cat
            }.toMutableList()
        }

        // Search filter
        if (currentSearchQuery.isNotBlank()) {
            val q = currentSearchQuery.lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) ||
                    it.category.lowercase().contains(q) ||
                    it.brand.lowercase().contains(q)
            }.toMutableList()
        }

        // Sort
        result = when (sortMode) {
            SortMode.PRICE_ASC -> result.sortedBy {
                it.price * (1 - it.discountPercentage / 100)
            }.toMutableList()
            SortMode.RATING_DESC -> result.sortedByDescending { it.rating }.toMutableList()
            SortMode.NONE -> result
        }

        filteredProducts.clear()
        filteredProducts.addAll(result)
        adapter.notifyDataSetChanged()

        updateProductCount(filteredProducts.size)
        showEmptyState(filteredProducts.isEmpty())
    }

    private fun updateProductCount(count: Int) {
        tvProductCount.text = if (count == 1) "1 product" else "$count products"
    }

    private fun showEmptyState(show: Boolean) {
        llEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        swipeRefresh.visibility = if (show) View.GONE else View.VISIBLE
    }

    // ── Shuffle ───────────────────────────────────────────────────────────
    private fun shuffleProducts() {
        Collections.shuffle(filteredProducts)
        adapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(0)

        Smartech.getInstance(WeakReference(this))
            .trackEvent("product_shuffle", hashMapOf())
    }

    private fun startCountdownTimer() {
        countDownTimer?.cancel()
        pbShuffle.progress = 0

        countDownTimer = object : CountDownTimer(COUNTDOWN_TOTAL_MS, COUNTDOWN_INTERVAL_MS) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000) + 1
                tvCountdown.text = "${secondsLeft}s"

                val progressPct = ((COUNTDOWN_TOTAL_MS - millisUntilFinished) * 100 /
                        COUNTDOWN_TOTAL_MS).toInt()
                pbShuffle.progress = progressPct
            }

            override fun onFinish() {
                tvCountdown.text = "0s"
                pbShuffle.progress = 100
                shuffleProducts()
                // Restart after shuffle
                startCountdownTimer()
            }
        }.start()
    }

    // ── SwipeRefresh ──────────────────────────────────────────────────────
    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(
            R.color.nc_primary, R.color.nc_accent
        )
        swipeRefresh.setOnRefreshListener {
            countDownTimer?.cancel()
            allProducts.clear()
            filteredProducts.clear()
            adapter.notifyDataSetChanged()
            llCategories.removeAllViews()
            selectedCategory = "All"
            currentSearchQuery = ""
            etSearch.setText("")
            sortMode = SortMode.NONE
            updateSortButtonStyles()
            fetchProducts()

            Smartech.getInstance(WeakReference(this))
                .trackEvent("product_refresh", hashMapOf())
        }
    }

    // ── Search ────────────────────────────────────────────────────────────
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s?.toString() ?: ""
                btnClearSearch.visibility = if (currentSearchQuery.isNotEmpty()) View.VISIBLE else View.GONE
                applyFilters()
            }
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                true
            } else false
        }

        btnClearSearch.setOnClickListener {
            etSearch.setText("")
            hideKeyboard()
        }
    }

    // ── Sort ──────────────────────────────────────────────────────────────
    private fun setupSortButtons() {
        btnSortPrice.setOnClickListener {
            sortMode = if (sortMode == SortMode.PRICE_ASC) SortMode.NONE else SortMode.PRICE_ASC
            updateSortButtonStyles()
            applyFilters()
        }
        btnSortRating.setOnClickListener {
            sortMode = if (sortMode == SortMode.RATING_DESC) SortMode.NONE else SortMode.RATING_DESC
            updateSortButtonStyles()
            applyFilters()
        }
    }

    private fun updateSortButtonStyles() {
        val activeBg = getDrawable(R.drawable.bg_discount_badge)
        val inactiveColor = getColor(R.color.nc_primary)
        val activeTextColor = getColor(R.color.white)

        if (sortMode == SortMode.PRICE_ASC) {
            btnSortPrice.background = activeBg
            btnSortPrice.setTextColor(activeTextColor)
        } else {
            btnSortPrice.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnSortPrice.setTextColor(inactiveColor)
        }

        if (sortMode == SortMode.RATING_DESC) {
            btnSortRating.background = activeBg
            btnSortRating.setTextColor(activeTextColor)
        } else {
            btnSortRating.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            btnSortRating.setTextColor(inactiveColor)
        }
    }

    // ── Cart ──────────────────────────────────────────────────────────────
    private fun setupCartButton() {
        flCart.setOnClickListener {
            if (cartCount > 0) {
                Toast.makeText(this, "Cart: $cartCount item(s)", Toast.LENGTH_SHORT).show()
            }
        }
        btnManualRefresh.setOnClickListener {
            countDownTimer?.cancel()
            shuffleProducts()
            startCountdownTimer()
        }
    }

    private fun addToCart(product: Product) {
        cartCount++
        tvCartCount.text = cartCount.toString()
        tvCartCount.visibility = View.VISIBLE
        Toast.makeText(this, "${product.title} added to cart!", Toast.LENGTH_SHORT).show()

        Smartech.getInstance(WeakReference(this))
            .trackEvent("add_to_cart", hashMapOf<String, Any>(
                "product_id" to product.id,
                "product_name" to product.title,
                "price" to product.price,
                "category" to product.category
            ))
    }

    // ── Shuffle button ────────────────────────────────────────────────────
    private fun setupShuffleButton() {
        btnShuffleNow.setOnClickListener {
            countDownTimer?.cancel()
            shuffleProducts()
            startCountdownTimer()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        tvProductCount.text = "Error loading products"
    }
}
