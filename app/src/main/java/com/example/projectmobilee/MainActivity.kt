package com.example.projectmobilee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmobilee.adapter.RestaurantAdapter
import com.example.projectmobilee.databinding.ActivityMainBinding
import com.example.projectmobilee.model.Restaurant
import com.example.projectmobilee.model.Order
import com.example.projectmobilee.model.CartItem
import com.example.projectmobilee.model.OrderStatus

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var restaurantAdapter: RestaurantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRestaurantList()
        setupNavigationButtons()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "ร้านอาหาร"
    }

    private fun setupRestaurantList() {
        restaurantAdapter = RestaurantAdapter(
            onItemClick = { restaurant ->
                if (restaurant.status == "ปิด") {
                    // แสดง Dialog เมื่อร้านปิด
                    AlertDialog.Builder(this)
                        .setTitle("ร้านปิด")
                        .setMessage("ร้านนี้ปิด ไม่สามารถสั่งอาหารได้ กรุณาเลือกร้านอื่น")
                        .setPositiveButton("ตกลง", null)
                        .show()
                } else {
                    // เปิดหน้ารายละเอียดร้านเมื่อร้านเปิด
                    val intent = Intent(this, RestaurantDetailActivity::class.java)
                    intent.putExtra("restaurant", restaurant)
                    startActivity(intent)
                }
            }
        )

        binding.restaurantRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = restaurantAdapter
        }

        loadRestaurants()
    }

    private fun loadRestaurants() {
        // ตัวอย่างข้อมูลร้านอาหาร
        val restaurants = listOf(
            Restaurant(
                id = "1",
                name = "ร้าน 1 อาหารไทย",
                description = "อาหารไทยแท้ดั้งเดิม มีเมนูแนะนำ: ต้มยำกุ้ง, แกงเขียวหวานไก่, ผัดไทยกุ้งสด, ข้าวผัดกระเพราไก่ไข่ดาว, ยำวุ้นเส้นกุ้ง, ส้มตำไทย, ปลาทอดน้ำปลา, แกงส้มชะอมกุ้ง, ผัดผักบุ้งไฟแดง",
                status = "เปิด (หมายเหตุ: ลาบหมูหมด)",
                phone = "เบอร์โทร:081-234-5678",
                rating = 4.5f,
                deliveryTime = 30,
                minOrder = 100.0,
                deliveryFee = 20.0,
                imageResId = R.drawable.la1
            ),
            Restaurant(
                id = "2",
                name = "ร้าน 2 อาหารตามสั่ง",
                description = "อาหารที่มีให้เลือกอย่างมากมาย มีเมนูแนะนำ: ข้าวพริกแกงไก่กรอบ, ข้าวผัดหมูกรอบ, ข้าวกะเทียมหมูกรอบ, ข้าวกะเพราหมูสับ",
                status = "เปิด (หมายเหตุ: ข้าวกะเพราหมูสับหมด, ผัดซีอิ้วไก่หมด)",
                phone = "เบอร์โทร:082-345-6789",
                rating = 4.8f,
                deliveryTime = 45,
                minOrder = 150.0,
                deliveryFee = 30.0,
                imageResId = R.drawable.la2
            ),
            Restaurant(
                id = "3",
                name = "ร้าน 3 ก๋วยเตี๋ยว",
                description = "ก๋วยเตี๋ยวสูตรเด็ด มีเมนูแนะนำ: ก๋วยเตี๋ยวหมูน้ำตก, ก๋วยเตี๋ยวต้มยำกุ้ง, ก๋วยเตี๋ยวเนื้อตุ๋น, ก๋วยเตี๋ยวเย็นตาโฟ, ก๋วยเตี๋ยวเรือ, บะหมี่หมูแดง, ข้าวขาหมู, ก๋วยเตี๋ยวหมูต้มยำ, ก๋วยเตี๋ยวเส้นใหญ่น้ำเงี้ยว, ขนมจีนน้ำยา",
                status = "ปิด",
                phone = "เบอร์โทร:083-456-7890",
                rating = 4.3f,
                deliveryTime = 25,
                minOrder = 80.0,
                deliveryFee = 15.0,
                imageResId = R.drawable.la3
            ),
            Restaurant(
                id = "4",
                name = "ร้าน 4 อาหารตามสั่ง",
                description = "อาหารตามสั่งรสจัดจ้าน มีเมนูแนะนำ: ผัดกระเพราหมูกรอบไข่ดาว, ผัดพริกแกงหมูกรอบ, ผัดซีอิ๊วเส้นใหญ่, ข้าวผัดกะเพราไก่, ไข่เจียวฟู, ข้าวผัดปู, ผัดผักรวม, ต้มยำไก่, ข้าวผัดอเมริกัน, ข้าวไข่ข้นกุ้ง, ข้าวมันไก่ทอด, ไก่ทอดกระเทียมพริกไทย",
                status = "เปิด (หมายเหตุ: เมนูที่มีหมูกรอบหมด, ข้าวไข่ข้นกุ้งหมด)",
                phone = "เบอร์โทร:084-567-8901",
                rating = 4.6f,
                deliveryTime = 35,
                minOrder = 120.0,
                deliveryFee = 25.0,
                imageResId = R.drawable.la4
            ),
            Restaurant(
                id = "5",
                name = "ร้าน 5 ข้าวมันไก่",
                description = "ข้าวมันไก่แสนอร่อย มีเมนูแนะนำ: ข้าวมันไก่ต้ม, ข้าวมันไก่ทอด, ข้าวมันไก่ผสม, ข้าวมันไก่พิเศษ, ข้าวมันไก่น้ำพริกเผา, ข้าวมันไก่น้ำจิ้มแจ่ว, ข้าวมันไก่ไข่ต้ม, ข้าวมันไก่น่องติดสะโพก, ข้าวมันไก่อกไม่มีกระดูก, ข้าวมันไก่แบบพิเศษ",
                status = "เปิด (หมายเหตุ: ข้าวมันไก่ทอดหมด)",
                phone = "เบอร์โทร:085-678-9012",
                rating = 4.4f,
                deliveryTime = 30,
                minOrder = 90.0,
                deliveryFee = 20.0,
                imageResId = R.drawable.la5
            ),
            Restaurant(
                id = "6",
                name = "ร้าน 6 ก๋วยเตี๊ยว",
                description = "ก๋วยเตี๊ยวนุ่มอร่อย มีเมนูแนะนำ: ก๋วยเตี๊ยวหมูน้ำใส, ก๋วยเตี๊ยวเนื้อตุ๋น, ก๋วยเตี๊ยวเป็ดพะโล้, เกี๊ยวน้ำหมูแดง, บะหมี่เกี๊ยวกุ้ง, ก๋วยเตี๊ยวต้มยำหมู, ก๋วยเตี๊ยวเย็นตาโฟ, ก๋วยเตี๊ยวหมูตุ๋น, ก๋วยเตี๊ยวหมูสับ, ก๋วยเตี๊ยวต้มยำทะเล",
                status = "เปิด",
                phone = "เบอร์โทร:086-789-0123",
                rating = 4.7f,
                deliveryTime = 35,
                minOrder = 100.0,
                deliveryFee = 25.0,
                imageResId = R.drawable.la6
            )
        )
        restaurantAdapter.submitList(restaurants)
    }

    private fun setupNavigationButtons() {
        binding.apply {
            cartButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, CartActivity::class.java))
            }

            orderHistoryButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, OrderHistoryActivity::class.java))
            }

            walletButton.setOnClickListener {
                startActivity(Intent(this@MainActivity, WalletActivity::class.java))
            }

            orderTrackingButton.setOnClickListener {
                // เรียกหน้าติดตามคำสั่งซื้อโดยตรง ไม่ต้องส่งข้อมูลตัวอย่าง
                // OrderTrackingActivity จะดึงข้อมูลจาก OrderManager เอง
                startActivity(Intent(this@MainActivity, OrderTrackingActivity::class.java))
            }
        }
    }
}