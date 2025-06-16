package com.example.projectmobilee

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmobilee.adapter.OrderHistoryAdapter
import com.example.projectmobilee.databinding.ActivityOrderHistoryBinding
import com.example.projectmobilee.model.OrderHistory
import com.example.projectmobilee.model.OrderHistoryManager
import com.example.projectmobilee.model.OrderStatus

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var orderAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ประวัติการสั่งซื้อ"

        setupOrderList()
    }

    private fun setupOrderList() {
        orderAdapter = OrderHistoryAdapter(
            orderHistories = OrderHistoryManager.getOrders(),
            onOrderClicked = { orderHistory ->
                showOrderDetails(orderHistory)
            },
            onDeleteClicked = { orderHistory ->
                showDeleteConfirmationDialog(orderHistory)
            }
        )

        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
            adapter = orderAdapter
        }

        updateEmptyState()
    }

    private fun updateOrderList() {
        orderAdapter.updateOrderHistories(OrderHistoryManager.getOrders())
        updateEmptyState()
    }
    
    // แสดง Dialog ยืนยันการลบ
    private fun showDeleteConfirmationDialog(orderHistory: OrderHistory) {
        AlertDialog.Builder(this)
            .setTitle("ลบประวัติการสั่งซื้อ")
            .setMessage("คุณต้องการลบประวัติการสั่งซื้อจากร้าน ${orderHistory.restaurantName} ใช่หรือไม่?")
            .setPositiveButton("ลบ") { _, _ ->
                // ลบประวัติและอัปเดตรายการ
                OrderHistoryManager.removeOrder(orderHistory.id)
                updateOrderList()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun showOrderDetails(orderHistory: OrderHistory) {
        val itemsText = orderHistory.items.joinToString("\n") { item ->
            "${item.name} x${item.quantity} = ฿%.2f".format(item.price * item.quantity)
        }

        AlertDialog.Builder(this)
            .setTitle("รายละเอียดคำสั่งซื้อ")
            .setMessage("""
                ร้าน: ${orderHistory.restaurantName}
                วันที่: ${orderHistory.orderDate}
                สถานะ: รับอาหารแล้ว
                
                รายการอาหาร:
                $itemsText
                
                ยอดรวม: ฿%.2f
            """.trimIndent().format(orderHistory.totalPrice))
            .setPositiveButton("ตกลง", null)
            .show()
    }

    private fun updateEmptyState() {
        val isEmpty = orderAdapter.itemCount == 0
        binding.emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.orderRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // อัปเดตรายการทุกครั้งที่กลับมาที่หน้านี้
        updateOrderList()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 