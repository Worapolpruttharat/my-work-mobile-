package com.example.projectmobilee.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.databinding.ItemMenuBinding
import com.example.projectmobilee.model.MenuItem

class MenuAdapter(
    private var menuItems: List<MenuItem>,
    private val onAddToCart: (MenuItem) -> Unit,
    private val onItemClicked: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: MenuItem) {
            binding.apply {
                menuName.text = menuItem.name
                menuDescription.text = menuItem.description
                menuPrice.text = "฿%.2f".format(menuItem.price)
                
                // ตรวจสอบว่าเมนูหมดหรือไม่
                if (menuItem.isOutOfStock) {
                    // ถ้าเมนูหมด ให้เปลี่ยนปุ่มเป็น "หมด" และตั้งค่าสีเป็นสีเทา
                    addToCartButton.text = "หมด"
                    addToCartButton.setBackgroundColor(Color.LTGRAY)
                    addToCartButton.isEnabled = false // ปิดการใช้งานปุ่ม
                } else {
                    // ถ้าเมนูไม่หมด ให้เป็น "สั่งซื้อ" ตามปกติ
                    addToCartButton.text = "สั่งซื้อ" 
                    addToCartButton.isEnabled = true // เปิดการใช้งานปุ่ม
                    addToCartButton.setOnClickListener {
                        onAddToCart(menuItem)
                    }
                }
                
                // TODO: ใช้ Glide โหลดรูปภาพ
                // Glide.with(menuImage)
                //     .load(menuItem.imageUrl)
                //     .into(menuImage)

                root.setOnClickListener { onItemClicked(menuItem) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount() = menuItems.size

    fun updateMenuItems(newItems: List<MenuItem>) {
        menuItems = newItems
        notifyDataSetChanged()
    }
} 