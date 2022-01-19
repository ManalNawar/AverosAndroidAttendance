package com.averos.als.positioningdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject


class MainAdapter(val homeFeed: JSONArray): RecyclerView.Adapter<CustomViewHolder>() {

    //val classesList = listOf("one","two","three")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val cellForRow = layoutInflater.inflate(R.layout.attandance_row, parent, false)
        return  CustomViewHolder(cellForRow)
    }


    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val jObject =  JSONObject(homeFeed[position].toString())
       // holder.ClassName.text= homeFeed.toString()
        holder.ClassName.text= jObject.getString("beacon")
        holder.createdAtDate.text= jObject.getString("created_at")
    }
}

class CustomViewHolder(val view : View): RecyclerView.ViewHolder(view){
    val ClassName: TextView
    val createdAtDate: TextView
    init {
        // Define click listener for the ViewHolder's View.
        ClassName = view.findViewById(R.id.ClassName)
        createdAtDate =view.findViewById(R.id.createdAtDate)
    }
}