package com.example.projectmobilee.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.databinding.ItemOrderHistoryBinding
import com.example.projectmobilee.model.OrderHistory
import com.example.projectmobilee.model.OrderStatus

class OrderHistoryAdapter(
    private var orderHistories: List<OrderHistory> = listOf(),
    private val onOrderClicked: (OrderHistory) -> Unit,
    private val onDeleteClicked: (OrderHistory) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(private val binding: ItemOrderHistoryBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(order: OrderHistory) {
            binding.apply {
                orderDateText.text = order.orderDate
                restaurantNameText.text = order.restaurantName
                
                // แสดงรายการอาหารที่สั่ง
                orderItemsText.text = order.getItemsText()
                
                // แสดงราคารวม
                totalPriceText.text = "฿%.2f".format(order.totalPrice)
                
                // แสดงสถานะเป็น "รับอาหารแล้ว" เนื่องจากเป็นประวัติ
                orderStatusText.text = "รับอาหารแล้ว"
                
                // เมื่อคลิกที่รายการ
                root.setOnClickListener { onOrderClicked(order) }
                
                // เมื่อคลิกที่ปุ่มลบ
                deleteButton.setOnClickListener { onDeleteClicked(order) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderHistories[position])
    }

    override fun getItemCount() = orderHistories.size
    
    // อัปเดตข้อมูลใหม่
    fun updateOrderHistories(newOrderHistories: List<OrderHistory>) {
        orderHistories = newOrderHistories
        notifyDataSetChanged()
    }
} 