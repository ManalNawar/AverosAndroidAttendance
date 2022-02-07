package com.averos.als.positioningdemo

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import okhttp3.*
import java.io.IOException
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class ListActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_attandance_list)
        var userEmail = SharedPrefManager.getInstance(this).user.email
        var userName = SharedPrefManager.getInstance(this).user.name
        var recyclerView = findViewById<RecyclerView>(R.id.recycle)

        print("kotlin respons Email is ======> $userEmail")
        print("kotlin respons Name is ======> $userName")

        //////////toolbar//////////////////
        var toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Records"
            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //var useremail = myintent.getStringExtra(useremail);
        // status bar color

        // status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //window.statusBarColor = this.resources.getColor(R.color.border_color)
        }

//        //recyclerView.setBackgroundColor(Color.BLUE)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        //recyclerView.adapter = MainAdapter()
//
//
//
//
//            //final TextView textView = (TextView) findViewById(R.id.respon);
//            //if everything is fine
//            val stringRequest: StringRequest = object : StringRequest(
//                Method.GET, URLs.URL_ATTENDANCE + LoginActivity.authResult.getUser().displayableId,
//                com.android.volley.Response.Listener { response ->
//                    //progressBar.setVisibility(View.VISIBLE);
//                    try {
//                        if (response.isNotEmpty()) {
//                            val body = response.toString();
//                            println("this is body"+body)
//                            val jObject =  JSONArray(body)
//                            println("this is body one"+jObject[1].toString())
//
//                            val homeFeed = jObject
//                            println(homeFeed)
//                            runOnUiThread{
//                                recyclerView.adapter =MainAdapter(homeFeed)
//                            }
//
//
//                        }else {
//                            Toast.makeText(
//                                applicationContext,
//                                "no idea",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                },
//                com.android.volley.Response.ErrorListener { error ->
//                    Toast.makeText(
//                        applicationContext,
//                        error.message,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }) {
//                @Throws(AuthFailureError::class)
//                override fun getHeaders(): Map<String, String> {
//                    val headers = HashMap<String, String>()
//                    print("this from header token${SharedPrefManager.getInstance(getApplicationContext()).token.token}")
//                    headers["Content-Type"] = "application/json; charset=utf-8"
//                    headers["Authorization"] = "Bearer${SharedPrefManager.getInstance(getApplicationContext()).token.token}"
//                    return headers
//                }
//            }
//            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest)
//
//
//        val client = OkHttpClient()
//        println("user id is "+LoginActivity.userID)
//        //"https://attend.ksauhs.com/api/attendance/user/" + LoginActivity.userID
//        val url =
//            HttpUrl.parse(URLs.URL_ATTENDANCE + LoginActivity.authResult.getUser().displayableId)!!
//                .newBuilder()
//                .build()
//        val request = Request.Builder()
//            .url(url)
//            .method("GET", null)
//            .build()
//        val call = client.newCall(request)
//        call.enqueue(object : Callback {
//            @kotlin.jvm.Throws(IOException::class)
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    val body = response.body()!!.string()
//                    println("this is body"+body)
//                    val jObject =  JSONArray(body)
//                    println("this is body one"+jObject[1].toString())
//
//                    val homeFeed = jObject
//                    println(homeFeed)
//                    runOnUiThread{
//                        recyclerView.adapter =MainAdapter(homeFeed)
//                    }
//
//
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                runOnUiThread {
//                    val toast =
//                        Toast.makeText(this@ListActivity, "failed to fetch api", Toast.LENGTH_LONG)
//                    toast.show()
//                }
//                println("failed to fetch api")
//            }
//        })
//    }
    // don't forget click listener for back button



    }
    override fun onSupportNavigateUp(): Boolean {
        var userEmail = SharedPrefManager.getInstance(this).user.email
        var userName = SharedPrefManager.getInstance(this).user.name
        print("kotlin respons Email is ======> $userEmail")
        print("kotlin respons Name is ======> $userName")
        onBackPressed()
        return true
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