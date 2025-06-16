package com.example.projectmobilee.model

/**
 * คลาสสำหรับจัดการประวัติการสั่งซื้ออาหาร
 * เก็บ จัดการ แสดง ประวัติการสั่งซื้อทั้งหมด
 */
object OrderHistoryManager {
    // เก็บรายการประวัติการสั่งซื้อ
    private val orderHistory = mutableListOf<OrderHistory>()
    
    // ดึงรายการประวัติทั้งหมด (เรียกใช้ใน OrderHistoryActivity)
    fun getOrders(): List<OrderHistory> {
        return orderHistory.toList().sortedByDescending { it.timestamp }
    }
    
    // ดึงรายการประวัติทั้งหมด
    fun getAllOrders(): List<OrderHistory> {
        return getOrders()
    }
    
    // เพิ่มประวัติการสั่งซื้อใหม่
    fun addOrder(order: OrderHistory) {
        orderHistory.add(order)
    }
    
    // ลบประวัติการสั่งซื้อ
    fun removeOrder(orderId: String) {
        orderHistory.removeIf { it.id == orderId }
    }
    
    // เคลียร์ประวัติทั้งหมด
    fun clearAllHistory() {
        orderHistory.clear()
    }
    
    // ตรวจสอบว่ามีประวัติหรือไม่
    fun hasHistory(): Boolean {
        return orderHistory.isNotEmpty()
    }
} 