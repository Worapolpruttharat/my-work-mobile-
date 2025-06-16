package com.example.projectmobilee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmobilee.adapter.CartAdapter
import com.example.projectmobilee.databinding.ActivityCartBinding
import com.example.projectmobilee.manager.CartManager
import com.example.projectmobilee.manager.WalletManager
import com.example.projectmobilee.model.CartItem
import com.example.projectmobilee.model.Order
import com.example.projectmobilee.model.OrderStatus
import com.example.projectmobilee.model.OrderManager
import java.text.SimpleDateFormat
import java.util.*

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupCartRecyclerView()
        setupCheckoutButton()
        updateCartUI()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "ตะกร้าของฉัน"
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(
            items = CartManager.getItems(),
            onQuantityChanged = { item, newQuantity ->
                CartManager.updateItemQuantity(item, newQuantity)
                updateCartUI()
            },
            onItemRemoved = { item ->
                showDeleteConfirmationDialog(item)
            }
        )

        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun updateCartUI() {
        val items = CartManager.getItems()
        cartAdapter.updateItems(items)

        if (items.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.cartContent.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.cartContent.visibility = View.VISIBLE
            binding.totalPriceText.text = "฿%.2f".format(CartManager.getTotalPrice())
        }
    }

    private fun setupCheckoutButton() {
        binding.checkoutButton.setOnClickListener {
            val walletManager = WalletManager.getInstance(this)
            val totalAmount = CartManager.getTotalPrice()

            if (!walletManager.hasEnoughBalance(totalAmount)) {
                showInsufficientBalanceDialog(totalAmount, walletManager.getBalance())
                return@setOnClickListener
            }

            // หักเงินจากกระเป๋าเงิน
            walletManager.deductBalance(totalAmount)

            // สร้างออเดอร์แยกตามร้าน และส่งไปหน้าติดตาม
            checkout()

            Toast.makeText(this, "สั่งซื้อสำเร็จ", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkout() {
        // ตรวจสอบว่ามีรายการในตะกร้าหรือไม่
        val items = CartManager.getItems()
        if (items.isEmpty()) {
            Toast.makeText(this, "กรุณาเพิ่มรายการอาหารในตะกร้า", Toast.LENGTH_SHORT).show()
            return
        }

        // สร้างออเดอร์แยกตามร้านอาหาร
        val itemsByRestaurant = CartManager.getItemsByRestaurant()
        
        // สร้างรายการออเดอร์
        val orders = itemsByRestaurant.map { (restaurantName, items) ->
            // หายอดรวมของแต่ละร้าน
            val restaurantTotal = items.sumOf { it.price * it.quantity }
            // หา restaurantId (ใช้ id ของ item แรก)
            val restaurantId = items.firstOrNull()?.restaurantId ?: ""
            
            // สร้างวันที่และเวลาปัจจุบัน
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            // สร้างเวลาที่คาดว่าจะได้รับสินค้า (เพิ่มไป 30 นาที)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 30)
            val estimatedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            
            Order(
                id = UUID.randomUUID().toString(),
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                items = items,
                totalPrice = restaurantTotal,
                orderDate = currentTime,
                deliveryTime = estimatedTime,
                status = OrderStatus.PENDING
            )
        }
        
        // เพิ่มออเดอร์เข้า OrderManager เพื่อให้หน้าอื่นเข้าถึงได้
        OrderManager.addOrders(orders)
        
        // เปิดหน้าติดตามออเดอร์พร้อมส่งข้อมูล
        val intent = Intent(this, OrderTrackingActivity::class.java)
        intent.putParcelableArrayListExtra("orders", ArrayList(orders))
        intent.putExtra("from_cart", true)
        startActivity(intent)
        
        // เคลียร์ตะกร้า
        CartManager.clearCart()
        updateCartUI()
    }

    private fun showDeleteConfirmationDialog(cartItem: CartItem) {
        AlertDialog.Builder(this)
            .setTitle("ลบรายการ")
            .setMessage("คุณต้องการลบ ${cartItem.name} ออกจากตะกร้าหรือไม่?")
            .setPositiveButton("ลบ") { _, _ ->
                CartManager.removeItem(cartItem)
                updateCartUI()
                Toast.makeText(this, "ลบรายการแล้ว", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun showInsufficientBalanceDialog(totalAmount: Double, currentBalance: Double) {
        val neededAmount = totalAmount - currentBalance
        AlertDialog.Builder(this)
            .setTitle("ยอดเงินไม่เพียงพอ")
            .setMessage("ยอดเงินในกระเป๋าของคุณไม่เพียงพอ\n\nยอดเงินปัจจุบัน: ฿%.2f\nยอดที่ต้องชำระ: ฿%.2f\nต้องเติมเงินเพิ่ม: ฿%.2f\n\nต้องการไปหน้าเติมเงินหรือไม่?"
                .format(currentBalance, totalAmount, neededAmount))
            .setPositiveButton("เติมเงิน") { _, _ ->
                startActivity(Intent(this, WalletActivity::class.java))
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun updateTotalPrice() {
        binding.totalPriceText.text = String.format("ราคารวม: ฿%.2f", CartManager.getTotalPrice())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 