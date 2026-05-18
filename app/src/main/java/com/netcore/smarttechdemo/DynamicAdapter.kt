package com.netcore.smarttechdemo

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference

class DynamicAdapter(
    private val context: Activity,
    private val productList: MutableList<Product>,
    private val onAddToCart: (Product, Int) -> Unit
) : RecyclerView.Adapter<DynamicAdapter.ProductViewHolder>() {

    // Track wishlist state per product id
    private val wishlistedIds = mutableSetOf<Int>()

    // ── Exposed helpers for Activity ──────────────────────────────────────
    fun updateData(newList: List<Product>) {
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Product = productList[position]

    // ── Hansel indexing entry-point ──────────────────────────────────────
    override fun onViewAttachedToWindow(holder: ProductViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.assignHanselIndex()
    }

    // ── RecyclerView overrides ────────────────────────────────────────────
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.eachitem, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product, position)
    }

    // ── ViewHolder ────────────────────────────────────────────────────────
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivProduct: ImageView = itemView.findViewById(R.id.iv_product)
        private val tvDiscountBadge: TextView = itemView.findViewById(R.id.tv_discount_badge)
        private val ivWishlist: ImageView = itemView.findViewById(R.id.iv_wishlist)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category_label)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_product_title)
        private val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        private val tvStock: TextView = itemView.findViewById(R.id.tv_stock)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvOriginalPrice: TextView = itemView.findViewById(R.id.tv_original_price)
        private val btnAddCart: MaterialButton = itemView.findViewById(R.id.btn_add_cart)

        // Hansel: persistent index stored in the ViewHolder
        private var hanselIndex: String? = null

        /** Called from onBindViewHolder — save index key for this product */
        fun saveHanselIndex(key: String) {
            hanselIndex = key
        }

        /** Called from onViewAttachedToWindow — push saved index to Hansel SDK */
        fun assignHanselIndex() {
            hanselIndex?.let { key ->
                Hansel.setCustomHanselIndex(itemView, key)
            }
        }

        fun bind(product: Product, @Suppress("UNUSED_PARAMETER") position: Int) {
            // 1. Save Hansel index (product title is the unique semantic key)
            saveHanselIndex(product.title)

            // 2. Product image via Picasso with placeholder
            Picasso.get()
                .load(product.thumbnail)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivProduct)

            // 3. Category label (capitalize first char)
            tvCategory.text = product.category.replaceFirstChar { it.uppercaseChar() }

            // 4. Title
            tvTitle.text = product.title

            // 5. Rating (one decimal)
            tvRating.text = "★ ${"%.1f".format(product.rating)}"

            // 6. Stock status
            when {
                product.stock <= 0 -> {
                    tvStock.text = "Out of stock"
                    tvStock.setTextColor(ContextCompat.getColor(context, R.color.nc_error))
                    btnAddCart.isEnabled = false
                    btnAddCart.alpha = 0.5f
                }
                product.stock < 5 -> {
                    tvStock.text = "Only ${product.stock} left"
                    tvStock.setTextColor(ContextCompat.getColor(context, R.color.nc_accent))
                    btnAddCart.isEnabled = true
                    btnAddCart.alpha = 1f
                }
                else -> {
                    tvStock.text = "In stock"
                    tvStock.setTextColor(ContextCompat.getColor(context, R.color.nc_success))
                    btnAddCart.isEnabled = true
                    btnAddCart.alpha = 1f
                }
            }

            // 7. Pricing
            val discountedPrice = product.price * (1 - product.discountPercentage / 100)
            tvPrice.text = "$${"%.2f".format(discountedPrice)}"

            if (product.discountPercentage >= 1.0) {
                tvOriginalPrice.visibility = View.VISIBLE
                tvOriginalPrice.text = "$${"%.2f".format(product.price)}"
                tvOriginalPrice.paintFlags = tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                tvDiscountBadge.visibility = View.VISIBLE
                tvDiscountBadge.text = "-${product.discountPercentage.toInt()}%"
            } else {
                tvOriginalPrice.visibility = View.GONE
                tvDiscountBadge.visibility = View.GONE
            }

            // 8. Wishlist heart state
            updateWishlistIcon(product.id)
            ivWishlist.setOnClickListener {
                if (wishlistedIds.contains(product.id)) {
                    wishlistedIds.remove(product.id)
                } else {
                    wishlistedIds.add(product.id)
                }
                updateWishlistIcon(product.id)
            }

            // 9. Add to Cart button
            btnAddCart.setOnClickListener {
                onAddToCart(product, bindingAdapterPosition)
            }
        }

        private fun updateWishlistIcon(productId: Int) {
            if (wishlistedIds.contains(productId)) {
                ivWishlist.setColorFilter(
                    ContextCompat.getColor(context, R.color.wishlist_active)
                )
            } else {
                ivWishlist.setColorFilter(
                    ContextCompat.getColor(context, R.color.secondaryTextColor)
                )
            }
        }
    }
}
