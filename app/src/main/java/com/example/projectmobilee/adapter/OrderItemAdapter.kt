package com.example.projectmobilee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.R
import com.example.projectmobilee.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter(private val items: List<CartItem>) : 
    RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.itemNameText)
        val quantityText: TextView = view.findViewById(R.id.itemQuantityText)
        val priceText: TextView = view.findViewById(R.id.itemPriceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameText.text = item.name
        holder.quantityText.text = "x${item.quantity}"
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("th", "TH"))
        formatter.maximumFractionDigits = 0
        holder.priceText.text = formatter.format(item.getTotalPrice())
    }

    override fun getItemCount() = items.size
} 