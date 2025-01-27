package com.netcore.smarttechdemo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcore.android.Smartech
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.util.Collections

class Dynamicview : AppCompatActivity() {

    private lateinit var mrecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var myAdapter: DynamicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamicview)

        // Track event
        val payload = HashMap<String, Any>()
        Smartech.getInstance(WeakReference(this)).trackEvent("dynamicview", payload)

        // Initialize RecyclerView and Retrofit
        initRecyclerView()
        initRetrofit()
    }

    private fun initRecyclerView() {
        mrecyclerView = findViewById(R.id.RecyclerView)
        layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }
        mrecyclerView.layoutManager = layoutManager
    }

    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val retrofitData = apiInterface.getProductData()

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val productList = responseBody?.products ?: listOf()

                    // Initialize adapter and set it to RecyclerView
                    myAdapter = DynamicAdapter(this@Dynamicview, productList)
                    mrecyclerView.adapter = myAdapter

                    // Shuffle the data after 3 seconds as an example
                    mrecyclerView.postDelayed({Collections.shuffle(productList) // Shuffle the list
                        myAdapter.notifyDataSetChanged()}, 3000)
                } else {
                    Log.d("Dynamicview", "API Response Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Log.e("Dynamicview", "API Call Failed: ${t.message}", t)
            }
        })
    }


}















































/*
class Dynamicview : AppCompatActivity() {

    private lateinit var mrecyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var userList: MutableList<DynamicModel>
    private lateinit var adapter: DynamicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dynamicview)

        val payload = HashMap<String, Any>()
        Smartech.getInstance(WeakReference(this)).trackEvent("dynamicview", payload)

        initData()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mrecyclerView = findViewById(R.id.RecyclerView)
        layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.VERTICAL
        }
        mrecyclerView.layoutManager = layoutManager
        adapter = DynamicAdapter(userList)
        mrecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun initData() {
        userList = mutableListOf()

        userList.add(DynamicModel(R.drawable.gi, "Anjali", "How are you?", "10:45 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.bo, "Brijesh", "I am fine", "15:08 pm", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.boy, "Sam", "You Know?", "1:02 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Divya", "How are you?", "12:55 pm", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Simran", "This is Easy", "13:50 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.boy, "Karan", "I am Don", "1:08 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.bo, "Sameer", "You Know this?", "4:02 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Baby", "How?", "11:55 pm", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Anjali", "How are you?", "10:45 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.bo, "Brijesh", "I am fine", "15:08 pm", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.boy, "Sam", "You Know?", "1:02 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Divya", "How are you?", "12:55 pm", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Simran", "This is Easy", "13:50 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.boy, "Karan", "I am Don", "1:08 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.bo, "Sameer", "You Know this?", "4:02 am", "_______________________________________"))
        userList.add(DynamicModel(R.drawable.gi, "Baby", "How?", "11:55 pm", "_______________________________________"))
    }
}*/
