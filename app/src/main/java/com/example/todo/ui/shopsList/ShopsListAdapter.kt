package com.example.todo.ui.shopsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import todo_database.Shop

class ShopsListAdapter(private val shopsList: ArrayList<Shop>) : RecyclerView.Adapter<ShopsListAdapter.ShopViewHolder>() {
    class ShopViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.shopElementName)
        val descriptionTextView: TextView = view.findViewById(R.id.shopElementDescription)
        val radiusTextView: TextView = view.findViewById(R.id.shopElementRadius)
        val coordinatesTextView: TextView = view.findViewById(R.id.shopElementCoordinates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_element, parent, false)
        return ShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.nameTextView.text = shopsList[position].name
        holder.descriptionTextView.text = shopsList[position].description
        holder.radiusTextView.text = shopsList[position].radius.toString()
        holder.coordinatesTextView.text = shopsList[position].coordinates
    }

    override fun getItemCount() = shopsList.size
}