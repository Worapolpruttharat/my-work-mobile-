package com.example.projectmobilee

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmobilee.adapter.OrderTrackingAdapter
import com.example.projectmobilee.databinding.ActivityOrderTrackingBinding
import com.example.projectmobilee.model.Order
import com.example.projectmobilee.model.OrderHistory
import com.example.projectmobilee.model.OrderHistoryManager
import com.example.projectmobilee.model.OrderManager
import com.example.projectmobilee.model.OrderStatus
import java.util.*

class OrderTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderTrackingBinding
    private var orders: List<Order> = listOf()
    private lateinit var adapter: OrderTrackingAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var comingFromCart = false // เพิ่มตัวแปรเพื่อบอกว่ามาจากหน้าตะกร้าหรือไม่

    companion object {
        const val EXTRA_ORDER = "extra_order"
        const val SIMULATION_DELAY_FAST = 1000L //  (ร้านเร็ว)
        const val SIMULATION_DELAY_MEDIUM = 3000L // (ร้านปานกลาง)
        const val SIMULATION_DELAY_SLOW = 5000L // (ร้านช้า)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ติดตามคำสั่งซื้อ"

        // ตรวจสอบว่ามาจากหน้าตะกร้าหรือไม่
        comingFromCart = intent.getBooleanExtra("from_cart", false)

        // รับข้อมูลออเดอร์และตั้งค่าการแสดงผล
        loadOrders()
        setupOrdersList()
        
        // เริ่มการจำลองสถานะออเดอร์ถ้ามีรายการสั่งอาหาร
        if (orders.isNotEmpty()) {
            startOrderStatusSimulation()
        }
    }

    private fun loadOrders() {
        // ตรวจสอบว่ามีการส่งค่า orders มาหรือไม่
        if (intent.hasExtra("orders")) {
            // กรณีที่มีการส่ง orders มาจากหน้าตะกร้าใหม่ๆ
            val newOrders = intent.getParcelableArrayListExtra<Order>("orders") ?: listOf()
            if (newOrders.isNotEmpty()) {
                // เคลียร์ออเดอร์เก่าก่อนเพิ่มอันใหม่เพื่อป้องกันการซ้ำซ้อน (ในกรณีที่มาจากหน้าตะกร้า)
                if (comingFromCart) {
                    OrderManager.clearAllOrders()
                }
                // เพิ่มเข้า OrderManager เพื่อให้หน้าอื่นเข้าถึงได้
                OrderManager.addOrders(newOrders)
            }
        } 
        // ถ้าไม่มี ตรวจสอบการส่งค่าแบบเดิม (กรณีเรียกจากหน้าอื่นที่ไม่ใช่ตะกร้า)
        else if (intent.hasExtra(EXTRA_ORDER)) {
            val singleOrder = intent.getParcelableExtra<Order>(EXTRA_ORDER)
            if (singleOrder != null) {
                OrderManager.addOrder(singleOrder)
            }
        }
        
        // ดึงข้อมูลจาก OrderManager เสมอ เพื่อให้แสดงข้อมูลล่าสุด
        orders = OrderManager.getAllOrders()
    }

    private fun setupOrdersList() {
        if (orders.isEmpty()) {
            // แสดงข้อความเมื่อไม่มีรายการสั่งอาหาร
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.ordersRecyclerView.visibility = View.GONE
            return
        }
        
        // มีรายการสั่งอาหาร แสดง RecyclerView
        binding.emptyStateLayout.visibility = View.GONE
        binding.ordersRecyclerView.visibility = View.VISIBLE
        
        adapter = OrderTrackingAdapter(
            orders = orders,
            onConfirmClick = { order -> showConfirmOrderDialog(order) }
        )
        
        binding.ordersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderTrackingActivity)
            adapter = this@OrderTrackingActivity.adapter
        }
    }

    private fun showConfirmOrderDialog(order: Order) {
        AlertDialog.Builder(this)
            .setTitle("ยืนยันการรับอาหาร")
            .setMessage("คุณได้รับอาหารจากร้าน ${order.restaurantName} เรียบร้อยแล้วใช่หรือไม่?")
            .setPositiveButton("ใช่") { _, _ ->
                completeOrder(order)
            }
            .setNegativeButton("ไม่", null)
            .show()
    }

    private fun completeOrder(completedOrder: Order) {
        // อัพเดทสถานะเป็น COMPLETED
        val updatedOrder = completedOrder.copy(status = OrderStatus.COMPLETED)
        
        // อัพเดทใน OrderManager
        OrderManager.updateOrder(updatedOrder)
        
        // บันทึกลงประวัติ
        saveOrderToHistory(completedOrder)
        
        // ลบออเดอร์นี้ออกจาก local list ทันที
        val updatedOrders = orders.filter { it.id != completedOrder.id }
        orders = updatedOrders
        
        // อัพเดทการแสดงผล
        adapter.updateOrders(orders)
        
        // ถ้าไม่มีออเดอร์เหลือแล้ว แสดงหน้าว่าง
        if (orders.isEmpty()) {
            showAllCompletedDialog()
        }
    }

    private fun showAllCompletedDialog() {
        AlertDialog.Builder(this)
            .setTitle("ออเดอร์ทั้งหมดเสร็จสมบูรณ์!")
            .setMessage("ทุกออเดอร์พร้อมรับแล้ว กรุณารับออร์เดอร์ของคุณ")
            .setPositiveButton("ตกลง", null) // แค่ปิดกล่องข้อความ ไม่ต้องเคลียร์ออเดอร์หรือปิดหน้า
            .setCancelable(false)
            .show()
    }

    private fun startOrderStatusSimulation() {
        // สร้าง Runnable สำหรับแต่ละออเดอร์ โดยมีเวลาจำลองแตกต่างกันตามความเร็วของร้าน
        orders.forEach { order ->
            val delay = when {
                order.restaurantId.hashCode() % 3 == 0 -> SIMULATION_DELAY_FAST // ร้านเร็ว
                order.restaurantId.hashCode() % 3 == 1 -> SIMULATION_DELAY_MEDIUM // ร้านปานกลาง
                else -> SIMULATION_DELAY_SLOW // ร้านช้า
            }
            
            val runnable = object : Runnable {
                override fun run() {
                    // ถ้าออเดอร์นี้ยังอยู่ในระบบ (ยังไม่ถูกเอาออกไป)
                    val currentOrder = orders.find { it.id == order.id } ?: return
                    
                    // อัพเดทสถานะออเดอร์นี้
                    val newStatus = when (currentOrder.status) {
                        OrderStatus.PENDING -> OrderStatus.ACCEPTED
                        OrderStatus.ACCEPTED -> OrderStatus.COOKING
                        OrderStatus.COOKING -> OrderStatus.READY
                        else -> currentOrder.status // คงสถานะเดิม
                    }
                    
                    // ถ้าสถานะเปลี่ยน
                    if (newStatus != currentOrder.status) {
                        // อัพเดทออเดอร์ในระบบ
                        val updatedOrder = currentOrder.copy(status = newStatus)
                        OrderManager.updateOrder(updatedOrder)
                        
                        // อัพเดท local list
                        val updatedOrders = orders.map {
                            if (it.id == order.id) updatedOrder else it
                        }
                        orders = updatedOrders
                        
                        // อัพเดทการแสดงผล
                        runOnUiThread {
                            adapter.updateOrders(orders)
                            
                            // ถ้าทุกออเดอร์พร้อมรับแล้ว แสดงข้อความ
                            if (orders.all { it.status == OrderStatus.READY }) {
                                showAllCompletedDialog()
                            }
                        }
                        
                        // ถ้ายังไม่ถึงสถานะสุดท้าย (READY) ให้วางแผนการอัพเดทครั้งต่อไป
                        if (newStatus != OrderStatus.READY) {
                            handler.postDelayed(this, delay)
                        }
                    }
                }
            }
            
            // เริ่มการจำลองสำหรับออเดอร์นี้
            handler.postDelayed(runnable, delay)
        }
    }

    private fun saveOrderToHistory(order: Order) {
        val orderHistory = OrderHistory(
            id = UUID.randomUUID().toString(),
            restaurantName = order.restaurantName,
            items = order.items,
            totalPrice = order.totalPrice,
            orderDate = order.orderDate,
            status = OrderStatus.COMPLETED
        )
        OrderHistoryManager.addOrder(orderHistory)
        
        // ลบออเดอร์ออกจาก OrderManager ทันที
        OrderManager.removeOrder(order.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 