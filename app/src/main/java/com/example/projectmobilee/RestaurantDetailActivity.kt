package com.example.projectmobilee

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectmobilee.adapter.MenuAdapter
import com.example.projectmobilee.databinding.ActivityRestaurantDetailBinding
import com.example.projectmobilee.manager.CartManager
import com.example.projectmobilee.model.CartItem
import com.example.projectmobilee.model.MenuItem
import com.example.projectmobilee.model.Restaurant

class RestaurantDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantDetailBinding
    private lateinit var menuAdapter: MenuAdapter
    private var currentRestaurant: Restaurant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // รับข้อมูลร้านอาหารจาก Intent
        currentRestaurant = intent.getParcelableExtra("restaurant")
        currentRestaurant?.let { restaurant ->
            supportActionBar?.title = restaurant.name
            binding.restaurantNameText.text = restaurant.name
            binding.restaurantDescriptionText.text = restaurant.description
            
            // กำหนดสีให้กับสถานะร้าน
            val greenColor = Color.parseColor("#00CC00") // สีเขียว
            val redColor = Color.RED // สีแดง
            
            // แสดงรูปภาพร้านอาหาร
            if (restaurant.imageResId != 0) {
                binding.restaurantImage.setImageResource(restaurant.imageResId)
                binding.restaurantImage.visibility = View.VISIBLE
            } else {
                binding.restaurantImage.visibility = View.GONE
            }
            
            if (restaurant.status.contains("เปิด")) {
                // กรณีร้านเปิด
                if (restaurant.status.contains("หมายเหตุ")) {
                    // มีหมายเหตุต้องแสดงสีแยกกัน
                    val fullText = restaurant.status
                    val spannableString = SpannableString(fullText)
                    
                    // กำหนดสีเริ่มต้นเป็นแดงทั้งหมด
                    spannableString.setSpan(
                        ForegroundColorSpan(redColor),
                        0,
                        fullText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    
                    // ค้นหาคำว่า "เปิด" ทุกตำแหน่งและกำหนดเป็นสีเขียว
                    var searchIndex = 0
                    while (true) {
                        val index = fullText.indexOf("เปิด", searchIndex)
                        if (index == -1) break
                        
                        spannableString.setSpan(
                            ForegroundColorSpan(greenColor),
                            index,
                            index + "เปิด".length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        
                        searchIndex = index + "เปิด".length
                    }
                    
                    // ตั้งค่าส่วนแรกถึงวงเล็บเปิดเป็นสีเขียว
                    val openEndIndex = fullText.indexOf("(")
                    if (openEndIndex > 0) {
                        spannableString.setSpan(
                            ForegroundColorSpan(greenColor),
                            0,
                            openEndIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    
                    binding.restaurantStatusText.text = spannableString
                } else {
                    // ไม่มีหมายเหตุ แสดงสีเขียวทั้งหมด
                    binding.restaurantStatusText.text = restaurant.status
                    binding.restaurantStatusText.setTextColor(greenColor)
                }
                
                // ชื่อร้านที่เปิดเป็นสีเขียว
                binding.restaurantNameText.setTextColor(greenColor)
            } else {
                // กรณีร้านปิด แสดงสีแดงทั้งหมด
                binding.restaurantStatusText.text = restaurant.status
                binding.restaurantStatusText.setTextColor(redColor)
                binding.restaurantNameText.setTextColor(Color.BLACK)
            }
            
            binding.restaurantPhoneText.text = restaurant.phone
        }

        setupMenuList()
    }

    private fun setupMenuList() {
        menuAdapter = MenuAdapter(
            menuItems = emptyList(),
            onAddToCart = { menuItem ->
                addToCart(menuItem, 1)
                Toast.makeText(this, "เพิ่ม ${menuItem.name} ลงตะกร้าแล้ว", Toast.LENGTH_SHORT).show()
            },
            onItemClicked = { menuItem ->
                showAddToCartDialog(menuItem)
            }
        )

        binding.menuRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RestaurantDetailActivity)
            adapter = menuAdapter
        }

        // โหลดข้อมูลเมนู
        loadMenuItems()
    }

    private fun loadMenuItems() {
        // เลือกเมนูตามประเภทร้าน
        val menuItems = when (currentRestaurant?.id) {
            "1" -> { // ร้าน 1 อาหารไทย
                listOf(
                    MenuItem("ต้มยำกุ้ง", "ต้มยำกุ้งน้ำข้น รสจัดจ้าน", 70.0),
                    MenuItem("แกงเขียวหวานไก่", "แกงเขียวหวานไก่ใส่มะเขือเปราะ", 55.0),
                    MenuItem("ผัดไทยกุ้งสด", "ผัดไทยกุ้งสดใส่ไข่ห่อ", 70.0),
                    MenuItem("ข้าวผัดกระเพราไก่ไข่ดาว", "ข้าวผัดกระเพราไก่ทอปด้วยไข่ดาว", 60.0),
                    MenuItem("ยำวุ้นเส้นกุ้ง", "ยำวุ้นเส้นรสเผ็ดกับกุ้งสด", 90.0),
                    MenuItem("แกงส้มชะอมกุ้ง", "แกงส้มชะอมกุ้งสด รสเปรี้ยวกำลังดี", 60.0),
                    MenuItem("ส้มตำไทย", "ส้มตำไทยรสจัดจ้าน", 65.0),
                    MenuItem("ลาบหมู", "ลาบหมูรสแซ่บ", 60.0, true),
                    MenuItem("น้ำตกหมู", "น้ำตกหมูสูตรพิเศษ [เมนูแนะนำ]", 60.0)
                )
            }
            "2" -> { // ร้าน 2 อาหารตามสั่ง
                listOf(
                    MenuItem("ข้าวกะเพราหมูสับ", "กะเพราหมูสับดีๆหอมๆร้อนๆ", 50.0, true),
                    MenuItem("ข้าวกะเทียมหมูกรอบ", "ข้าวกะเทียมหมูกรอบดีๆ", 60.0),
                    MenuItem("ข้าวผัดหมูกรอบ", "ข้าวผัดหอมๆร้อนๆกินพร้อมกับหมูกรอบ", 55.0),
                    MenuItem("ผัดซีอิ๊วไก่", "ผัดซ๊อิ้วคั่วหอมๆกับไก่อร่อยๆ", 50.0, true),
                    MenuItem("ข้าวพริกแกงไก่กรอบ", "พริกแกงร้อนๆเข้ากับไก่กรอบ", 60.0),
                    MenuItem("ผัดมาม่าหมู", "ผัดมาม่าร้อนๆ", 45.0),
                    MenuItem("ข้าวผัดไก่", "ข้าวผัดร้อนๆ", 50.0),
                )
            }
            "3" -> { // ร้าน 3 ก๋วยเตี๋ยว (ปิด)
                listOf(
                    MenuItem("ก๋วยเตี๋ยวหมูน้ำตก", "ก๋วยเตี๋ยวหมูสับน้ำตกสูตรดั้งเดิม", 60.0, true),
                    MenuItem("ก๋วยเตี๋ยวต้มยำกุ้ง", "ก๋วยเตี๋ยวต้มยำกุ้งน้ำข้น", 70.0, true),
                    MenuItem("ก๋วยเตี๋ยวเนื้อตุ๋น", "ก๋วยเตี๋ยวเนื้อตุ๋นเปื่อยนุ่ม", 75.0, true),
                    MenuItem("ก๋วยเตี๋ยวเย็นตาโฟ", "ก๋วยเตี๋ยวเย็นตาโฟรสอร่อย", 65.0, true),
                    MenuItem("บะหมี่หมูแดง", "บะหมี่หมูแดงน้ำใส", 55.0, true),
                    MenuItem("ก๋วยเตี๋ยวหมูตุ๋น", "ก๋วยเตี๋ยวหมูตุ๋นกระดูกหมู", 60.0, true),
                    MenuItem("ก๋วยเตี๋ยวเป็ด", "ก๋วยเตี๋ยวเป็ดพะโล้", 65.0, true),
                    MenuItem("ก๋วยเตี๋ยวไก่ตุ๋น", "ก๋วยเตี๋ยวไก่ตุ๋นมะระ", 60.0, true),
                    MenuItem("ก๋วยเตี๋ยวเนื้อน้ำใส", "ก๋วยเตี๋ยวเนื้อน้ำใสเนื้อนุ่ม", 70.0, true),
                    MenuItem("ก๋วยเตี๋ยวเซี่ยงไฮ้", "ก๋วยเตี๋ยวเซี่ยงไฮ้ [เมนูพิเศษ]", 80.0, true)
                )
            }
            "4" -> { // ร้าน 4 อาหารตามสั่ง
                listOf(
                    MenuItem("ผัดกระเพราหมูกรอบไข่ดาว", "ผัดกระเพรากับหมูกรอบ และไข่ดาว", 70.0, true),
                    MenuItem("ผัดพริกแกงหมูกรอบ", "ผัดพริกแกงกับหมูกรอบและผักต่างๆ", 70.0, true),
                    MenuItem("ผัดซีอิ๊วเส้นใหญ่", "ผัดซีอิ๊วเส้นใหญ่ใส่ไข่และหมูสับ", 60.0),
                    MenuItem("ข้าวผัดกะเพราไก่", "ข้าวผัดกะเพราไก่สับ", 60.0),
                    MenuItem("ข้าวผัดปู", "ข้าวผัดปูรสเด็ด", 80.0),
                    MenuItem("ผัดผักรวมมิตร", "ผัดผักรวมมิตรกับหมูหมัก", 65.0),
                    MenuItem("ข้าวผัดอเมริกัน", "ข้าวผัดอเมริกัน ไส้กรอก แฮม และไข่ดาว", 70.0),
                    MenuItem("ข้าวไข่ข้นกุ้ง", "ข้าวไข่ข้นกุ้งสด", 65.0, true),
                    MenuItem("ต้มยำไก่", "ต้มยำไก่น้ำใส", 55.0),
                    MenuItem("สปาเกตตี้คาโบนาร่า", "สปาเกตตี้คาโบนาร่าครีมข้น [เมนูพิเศษ]", 85.0)
                )
            }
            "5" -> { // ร้าน 5 ข้าวมันไก่
                listOf(
                    MenuItem("ข้าวมันไก่ต้ม", "ข้าวมันไก่ต้มสูตรเด็ด", 50.0),
                    MenuItem("ข้าวมันไก่ทอด", "ข้าวมันไก่ทอดกรอบ", 55.0, true),
                    MenuItem("ข้าวมันไก่ผสม", "ข้าวมันไก่ผสมทั้งต้มและทอด", 60.0),
                    MenuItem("ข้าวมันไก่พิเศษ", "ข้าวมันไก่พิเศษเสิร์ฟพร้อมซุปและขนมหวาน", 70.0),
                    MenuItem("ข้าวมันไก่น้ำจิ้มแจ่ว", "ข้าวมันไก่คู่กับน้ำจิ้มแจ่วสูตรพิเศษ", 55.0),
                    MenuItem("ข้าวมันไก่ไข่ต้ม", "ข้าวมันไก่เสิร์ฟพร้อมไข่ต้ม", 65.0),
                    MenuItem("ข้าวมันไก่อกไม่มีกระดูก", "ข้าวมันไก่อกล้วนไม่มีกระดูก", 60.0),
                    MenuItem("ข้าวมันไก่แบบพิเศษ", "ข้าวมันไก่เสิร์ฟพร้อมตับไก่ต้ม น้ำซุป และซอสเพิ่ม [เมนูแนะนำ]", 75.0)
                )
            }
            "6" -> { // ร้าน 6 ก๋วยเตี๋ยว
                listOf(
                    MenuItem("ก๋วยเตี๊ยวหมูน้ำใส", "ก๋วยเตี๊ยวเส้นเล็กหมูสไลด์น้ำใสหวานหอม", 50.0),
                    MenuItem("ก๋วยเตี๊ยวเนื้อตุ๋น", "ก๋วยเตี๊ยวเนื้อตุ๋นเปื่อยนุ่ม", 60.0),
                    MenuItem("ก๋วยเตี๊ยวเป็ดพะโล้", "ก๋วยเตี๊ยวเป็ดพะโล้สูตรเด็ด", 70.0),
                    MenuItem("เกี๊ยวน้ำหมูแดง", "เกี๊ยวน้ำหมูแดงสูตรฮ่องกง", 60.0),
                    MenuItem("บะหมี่เกี๊ยวกุ้ง", "บะหมี่เกี๊ยวกุ้งน้ำหวานพิเศษ", 60.0),
                    MenuItem("ก๋วยเตี๊ยวต้มยำหมู", "ก๋วยเตี๊ยวต้มยำหมูน้ำข้น", 60.0),
                    MenuItem("ก๋วยเตี๊ยวเย็นตาโฟ", "ก๋วยเตี๊ยวเย็นตาโฟทะเล", 65.0),
                    MenuItem("ก๋วยเตี๊ยวหมูตุ๋น", "ก๋วยเตี๊ยวหมูตุ๋นยาจีน", 65.0),
                    MenuItem("ก๋วยเตี๊ยวหมูสับ", "ก๋วยเตี๊ยวหมูสับใส่กระเทียม", 55.0),
                    MenuItem("ก๋วยเตี๊ยวต้มยำทะเล", "ก๋วยเตี๊ยวต้มยำทะเลรวมมิตร [เมนูพิเศษ]", 70.0)
                )
            }
            else -> {
                // เมนูอาหารเริ่มต้น ถ้าไม่มีร้านอื่น
                listOf(
                    MenuItem("ข้าวผัดหมู", "ข้าวผัดหมูสูตรพิเศษ", 50.0),
                    MenuItem("ต้มยำกุ้ง", "ต้มยำกุ้งน้ำข้น", 70.0),
                    MenuItem("ผัดกะเพราหมู", "ผัดกะเพราหมูไข่ดาว", 60.0),
                    MenuItem("ผัดผักรวม", "ผัดผักรวมน้ำมันหอย", 40.0),
                    MenuItem("ข้าวผัดไข่", "ข้าวผัดไข่ใส่ต้นหอม", 45.0),
                    MenuItem("ข้าวผัดกระเทียม", "ข้าวผัดกระเทียมหมู", 50.0),
                    MenuItem("ข้าวไข่เจียว", "ข้าวไข่เจียวฟูหมูสับ", 45.0),
                    MenuItem("ข้าวหมูทอดกระเทียม", "ข้าวหมูทอดกระเทียมพริกไทย", 60.0),
                    MenuItem("ข้าวไก่ทอดกระเทียม", "ข้าวไก่ทอดกระเทียมพริกไทย", 60.0),
                    MenuItem("ข้าวผัดสับปะรด", "ข้าวผัดสับปะรดกุ้ง [เมนูพิเศษ]", 70.0)
                )
            }
        }

        menuAdapter.updateMenuItems(menuItems)
    }

    private fun showAddToCartDialog(menuItem: MenuItem) {
        AlertDialog.Builder(this)
            .setTitle("เพิ่มลงตะกร้า")
            .setMessage("""
                ${menuItem.name}
                ราคา: ฿%.2f
                
                จำนวนที่ต้องการ:
            """.trimIndent().format(menuItem.price))
            .setPositiveButton("เพิ่มลงตะกร้า") { dialog, _ ->
                val quantity = 1 // จำนวนเริ่มต้น
                addToCart(menuItem, quantity)
                dialog.dismiss()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private fun addToCart(menuItem: MenuItem, quantity: Int) {
        currentRestaurant?.let { restaurant ->
            val cartItem = CartItem(
                id = System.currentTimeMillis().toString(),
                name = menuItem.name,
                price = menuItem.price.toDouble(),
                quantity = quantity,
                restaurantId = restaurant.id,
                restaurantName = restaurant.name
            )
            CartManager.addItem(cartItem)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 