package com.example.projectmobilee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.R
import com.example.projectmobilee.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private var groupedItems: List<Any> = listOf()

    init {
        groupItemsByRestaurant()
    }

    private fun groupItemsByRestaurant() {
        val grouped = mutableListOf<Any>()
        val restaurantGroups = items.groupBy { it.restaurantName }
        
        restaurantGroups.forEach { (restaurantName, items) ->
            grouped.add(restaurantName)
            grouped.addAll(items)
        }
        
        groupedItems = grouped
    }

    class RestaurantHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameText: TextView = view.findViewById(R.id.restaurantNameText)
    }

    class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.menuName)
        val priceText: TextView = view.findViewById(R.id.menuPrice)
        val quantityText: TextView = view.findViewById(R.id.quantityText)
        val increaseButton: ImageButton = view.findViewById(R.id.increaseButton)
        val decreaseButton: ImageButton = view.findViewById(R.id.decreaseButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun getItemViewType(position: Int): Int {
        return when (groupedItems[position]) {
            is String -> VIEW_TYPE_HEADER
            is CartItem -> VIEW_TYPE_ITEM
            else -> throw IllegalArgumentException("ประเภทข้อมูลไม่ถูกต้อง")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_restaurant_header, parent, false)
                RestaurantHeaderViewHolder(view)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_cart, parent, false)
                CartItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("ประเภทข้อมูลไม่ถูกต้อง")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = groupedItems[position]) {
            is String -> {
                val headerHolder = holder as RestaurantHeaderViewHolder
                headerHolder.restaurantNameText.text = item
            }
            is CartItem -> {
                val itemHolder = holder as CartItemViewHolder
                itemHolder.nameText.text = item.name
                itemHolder.priceText.text = "฿%.2f".format(item.price)
                itemHolder.quantityText.text = item.quantity.toString()

                itemHolder.increaseButton.setOnClickListener {
                    onQuantityChanged(item, item.quantity + 1)
                }

                itemHolder.decreaseButton.setOnClickListener {
                    if (item.quantity > 1) {
                        onQuantityChanged(item, item.quantity - 1)
                    }
                }

                itemHolder.deleteButton.setOnClickListener {
                    onItemRemoved(item)
                }
            }
        }
    }

    override fun getItemCount() = groupedItems.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        groupItemsByRestaurant()
        notifyDataSetChanged()
    }
} 