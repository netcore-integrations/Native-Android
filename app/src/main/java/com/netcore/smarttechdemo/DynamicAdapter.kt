package com.netcore.smarttechdemo
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso
import io.hansel.hanselsdk.Hansel


class DynamicAdapter(val context : Activity, val productArrayList : List<Product>) :
    RecyclerView.Adapter<DynamicAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.eachitem, parent, false)


        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return productArrayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = productArrayList[position]
        holder.title.text = currentItem.title
        // image view , how to show image in image view if the image is in form of url, 3rd Party Library
        // Picasso
        Picasso.get().load(currentItem.thumbnail).into(holder.image);

        //string_for_hansel_index for dynamicview
        holder.saveHanselIndex(currentItem.title);
    }

   // below function used assing hansel index
    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.assignHanselIndex()
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var title : TextView
        var image : ShapeableImageView
        private var hanselIndex: String? = null



        init {
            title = itemView.findViewById(R.id.productTitle)
            image = itemView.findViewById(R.id.productImage)
        }


        // Save hansel index
        fun saveHanselIndex(hanselIndex: String) {
            this.hanselIndex = hanselIndex
        }


        // assign hansel index
        fun assignHanselIndex() {
            hanselIndex?.let {
                Hansel.setCustomHanselIndex(itemView, it)
            }
        }


    }



}

















































/*
class DynamicAdapter(private val userList: List<DynamicModel>) : RecyclerView.Adapter<DynamicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = userList[position]
        holder.setData(model.imageview, model.textview1, model.textview2, model.textview3, model.divider)
        holder.saveHanselIndex(model.textview1)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.assignHanselIndex()
    }

    override fun getItemCount(): Int = userList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageview)
        private val textView: TextView = itemView.findViewById(R.id.textview)
        private val textView2: TextView = itemView.findViewById(R.id.textview2)
        private val textView3: TextView = itemView.findViewById(R.id.textview3)
        private val divider: TextView = itemView.findViewById(R.id.Divider)

        private var hanselIndex: String? = null

        fun saveHanselIndex(hanselIndex: String) {
            this.hanselIndex = hanselIndex
        }

        fun assignHanselIndex() {
            hanselIndex?.let {
                Hansel.setCustomHanselIndex(itemView, it)
            }
        }

        fun setData(resource: Int, name: String, msg: String, time: String, line: String) {
            imageView.setImageResource(resource)
            textView.text = name
            textView2.text = msg
            textView3.text = time
            divider.text = line
        }
    }
}
*/
