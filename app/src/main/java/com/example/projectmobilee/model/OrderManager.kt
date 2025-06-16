package com.example.projectmobilee.model

/**
 * คลาสสำหรับจัดการออเดอร์ที่กำลังดำเนินการในปัจจุบัน 
 * เพื่อให้สามารถเข้าถึงจากทุกส่วนของแอป
 */
object OrderManager {
    // รายการออเดอร์ปัจจุบันทั้งหมดในระบบ
    private val currentOrders = mutableListOf<Order>()
    
    // ขอรายการออเดอร์ปัจจุบันทั้งหมด (เป็น copy เพื่อไม่ให้แก้ไขโดยตรง)
    fun getAllOrders(): List<Order> {
        return currentOrders.toList()
    }
    
    // เพิ่มออเดอร์ใหม่เข้าระบบ
    fun addOrder(order: Order) {
        currentOrders.add(order)
    }
    
    // เพิ่มหลายออเดอร์พร้อมกัน
    fun addOrders(orders: List<Order>) {
        currentOrders.addAll(orders)
    }
    
    // อัพเดทสถานะของออเดอร์ที่มีอยู่
    fun updateOrder(updatedOrder: Order) {
        val index = currentOrders.indexOfFirst { it.id == updatedOrder.id }
        if (index != -1) {
            currentOrders[index] = updatedOrder
        }
    }
    
    // ลบออเดอร์ออกจากระบบ (เมื่อเสร็จสมบูรณ์หรือยกเลิก)
    fun removeOrder(orderId: String) {
        currentOrders.removeIf { it.id == orderId }
    }
    
    // เคลียร์ออเดอร์ทั้งหมด
    fun clearAllOrders() {
        currentOrders.clear()
    }
    
    // ตรวจสอบว่ามีออเดอร์ในระบบหรือไม่
    fun hasOrders(): Boolean {
        return currentOrders.isNotEmpty()
    }
} 