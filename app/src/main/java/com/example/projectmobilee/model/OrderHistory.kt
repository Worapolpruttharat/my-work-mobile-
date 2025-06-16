package com.example.projectmobilee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ข้อมูลประวัติการสั่งซื้อ
 */
@Parcelize
data class OrderHistory(
    val id: String,
    val restaurantName: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val orderDate: String,
    val status: OrderStatus = OrderStatus.COMPLETED,
    // เพิ่ม timestamp เพื่อใช้ในการเรียงลำดับ
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {
    
    // แสดงรายการอาหารเป็นข้อความ
    fun getItemsText(): String {
        return items.joinToString(", ") { item ->
            "${item.name} x${item.quantity}"
        }
    }
    
    // คำนวณราคารวมของแต่ละรายการ
    fun calculateTotalPrice(): Double {
        return items.sumOf { it.price * it.quantity }
    }
    
    // สร้างจาก Order
    companion object {
        fun fromOrder(order: Order): OrderHistory {
            return OrderHistory(
                id = order.id,
                restaurantName = order.restaurantName,
                items = order.items,
                totalPrice = order.totalPrice,
                orderDate = order.orderDate,
                status = OrderStatus.COMPLETED
            )
        }
    }
} 