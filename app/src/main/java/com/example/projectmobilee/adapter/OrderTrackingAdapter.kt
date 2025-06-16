package com.example.projectmobilee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.R
import com.example.projectmobilee.model.Order
import com.example.projectmobilee.model.OrderStatus

class OrderTrackingAdapter(
    private var orders: List<Order>,
    private val onConfirmClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderTrackingAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restaurantNameText: TextView = view.findViewById(R.id.restaurantNameText)
        val deliveryTimeText: TextView = view.findViewById(R.id.deliveryTimeText)
        val orderDateText: TextView = view.findViewById(R.id.orderDateText)
        val totalPriceText: TextView = view.findViewById(R.id.totalPriceText)
        val statusMessageText: TextView = view.findViewById(R.id.statusMessageText)
        val itemsRecyclerView: RecyclerView = view.findViewById(R.id.orderItemsRecyclerView)
        val confirmButton: Button = view.findViewById(R.id.confirmButton)

        // สถานะการติดตาม
        val step1Icon: ImageView = view.findViewById(R.id.step1Icon)
        val step2Icon: ImageView = view.findViewById(R.id.step2Icon)
        val step3Icon: ImageView = view.findViewById(R.id.step3Icon)
        val step1Line: View = view.findViewById(R.id.step1Line)
        val step2Line: View = view.findViewById(R.id.step2Line)
        val step1Text: TextView = view.findViewById(R.id.step1Text)
        val step2Text: TextView = view.findViewById(R.id.step2Text)
        val step3Text: TextView = view.findViewById(R.id.step3Text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_tracking, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        
        // แสดงข้อมูลพื้นฐานของออเดอร์
        holder.restaurantNameText.text = order.restaurantName
        holder.deliveryTimeText.text = "เวลารับอาหาร: ${order.deliveryTime}"
        holder.orderDateText.text = "วันที่สั่ง: ${order.orderDate}"
        holder.totalPriceText.text = "ราคารวม: ฿%.2f".format(order.totalPrice)
        
        // แสดงรายการอาหาร
        holder.itemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = OrderItemAdapter(order.items)
        }
        
        // อัพเดทสถานะการติดตาม
        updateStatusUI(holder, order.status)
        
        // จัดการปุ่มยืนยัน
        holder.confirmButton.visibility = if (order.status == OrderStatus.READY) View.VISIBLE else View.GONE
        holder.confirmButton.setOnClickListener {
            onConfirmClick(order)
        }
    }

    private fun updateStatusUI(holder: OrderViewHolder, status: OrderStatus) {
        val context = holder.itemView.context
        val completedColor = ContextCompat.getColor(context, R.color.success)
        val pendingColor = ContextCompat.getColor(context, R.color.secondary_text)

        // แสดงข้อความสถานะ
        holder.statusMessageText.text = getStatusDescription(status)

        // อัพเดทไอคอนและสีตามสถานะ
        holder.step1Icon.setColorFilter(if (status.ordinal >= OrderStatus.PENDING.ordinal) completedColor else pendingColor)
        holder.step2Icon.setColorFilter(if (status.ordinal >= OrderStatus.COOKING.ordinal) completedColor else pendingColor)
        holder.step3Icon.setColorFilter(if (status.ordinal >= OrderStatus.READY.ordinal) completedColor else pendingColor)

        holder.step1Line.setBackgroundColor(if (status.ordinal >= OrderStatus.COOKING.ordinal) completedColor else pendingColor)
        holder.step2Line.setBackgroundColor(if (status.ordinal >= OrderStatus.READY.ordinal) completedColor else pendingColor)

        holder.step1Text.setTextColor(if (status.ordinal >= OrderStatus.PENDING.ordinal) completedColor else pendingColor)
        holder.step2Text.setTextColor(if (status.ordinal >= OrderStatus.COOKING.ordinal) completedColor else pendingColor)
        holder.step3Text.setTextColor(if (status.ordinal >= OrderStatus.READY.ordinal) completedColor else pendingColor)
    }

    private fun getStatusDescription(status: OrderStatus): String {
        return when (status) {
            OrderStatus.PENDING -> "กำลังรอร้านรับออเดอร์ของคุณ"
            OrderStatus.ACCEPTED -> "ร้านรับออเดอร์แล้ว กำลังเตรียมอาหาร"
            OrderStatus.COOKING -> "กำลังปรุงอาหารของคุณ"
            OrderStatus.READY -> "อาหารพร้อมเสิร์ฟแล้ว กรุณารับอาหารที่ร้าน"
            OrderStatus.COMPLETED -> "ออเดอร์เสร็จสมบูรณ์"
            OrderStatus.CANCELLED -> "ออเดอร์ถูกยกเลิก"
        }
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }
} 