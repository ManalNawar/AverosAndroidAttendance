package com.averos.als.positioningdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import java.io.IOException
import com.google.gson.annotations.SerializedName
import org.json.JSONArray


class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_attandance_list)
        var recyclerView = findViewById<RecyclerView>(R.id.recycle)
        //recyclerView.setBackgroundColor(Color.BLUE)
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = MainAdapter()

        val client = OkHttpClient()
        val url =
            HttpUrl.parse("https://attend.ksauhs.com/api/attendance/user/" + LoginActivity.userID)!!
                .newBuilder()
                .build()
        val request = Request.Builder()
            .url(url)
            .method("GET", null)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            @kotlin.jvm.Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body()!!.string()
                    println("this is body"+body)
                    val jObject =  JSONArray(body)
                    println("this is body one"+jObject[1].toString())

                    val homeFeed = jObject
                    //println(homeFeed)
                    runOnUiThread{
                        recyclerView.adapter =MainAdapter(homeFeed)
                    }


                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    val toast =
                        Toast.makeText(this@ListActivity, "failed to fetch api", Toast.LENGTH_LONG)
                    toast.show()
                }
                println("failed to fetch api")
            }
        })
    }


}


class HomeFeed(){
//    @SerializedName("id")
//    private val id: Int? = null
//
//    @SerializedName("user_id")
//    private val user_id: Int? = null
//
//    @SerializedName("block_id")
//    private val block_id: Int? = null
//
//    @SerializedName("college_id")
//    private val college_id: String? = null
//
//    @SerializedName("coords")
//    private val coords: String? = null
//
//    @SerializedName("beacon")
//    private val beacon: String? = null
//
//    @SerializedName("created_at")
//    private val created_at: String? = null
//
//    @SerializedName("updated_at")
//    private val updated_at: String? = null
//
//    @SerializedName("deleted_at")
//    private val deleted_at: String? = null
//
//
//    fun get_Updated_at(): String? {
//        return updated_at
//    }
//
//    fun get_deleted_at(): String? {
//        return deleted_at
//    }
//
//    fun get_created_at(): String? {
//        return created_at
//    }
//
//    fun get_beacon(): String? {
//        return beacon
//    }
//
//    fun get_coords(): String? {
//        return coords
//    }
//
//    fun get_college_id(): String? {
//        return college_id
//    }
//
//    fun get_block_id(): Int? {
//        return block_id
//    }
//
//    fun get_user_id(): Int? {
//        return user_id
//    }
//
//    fun get_Id(): Int? {
//        return id
//    }

}
//val beacon: String, val createdAt: String
//class becaon(val beacon: String)
//class createdAt(val createdAt: Int)