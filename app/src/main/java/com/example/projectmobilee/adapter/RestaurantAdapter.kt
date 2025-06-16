package com.example.projectmobilee.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobilee.databinding.ItemRestaurantBinding
import com.example.projectmobilee.model.Restaurant

class RestaurantAdapter(
    private val onItemClick: (Restaurant) -> Unit
) : ListAdapter<Restaurant, RestaurantAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RestaurantViewHolder(
        private val binding: ItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(restaurant: Restaurant) {
            binding.apply {
                // รีเซ็ตสีข้อความให้เป็นสีดำ
                restaurantName.setTextColor(Color.BLACK)
                
                restaurantName.text = restaurant.name
                restaurantPhone.text = "เบอร์โทร: ${restaurant.phone}"
                
                // แสดงรูปภาพร้านอาหาร
                if (restaurant.imageResId != 0) {
                    restaurantImage.setImageResource(restaurant.imageResId)
                    restaurantImage.visibility = View.VISIBLE
                } else {
                    restaurantImage.visibility = View.GONE
                }
                
                // สีสำหรับสถานะร้าน
                val greenColor = Color.parseColor("#00CC00") // สีเขียว
                val redColor = Color.RED // สีแดง
                
                if (restaurant.status.contains("เปิด")) {
                    // ถ้ามีหมายเหตุ ต้องแสดงข้อความแยกสี
                    if (restaurant.status.contains("หมายเหตุ")) {
                        val fullText = restaurant.status
                        val spannableString = SpannableString(fullText)
                        
                        // กำหนดสีเริ่มต้นเป็นแดงสำหรับทั้งข้อความ
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
                        
                        // กำหนดข้อความที่มีการจัดรูปแบบแล้ว
                        restaurantStatus.text = spannableString
                    } else {
                        // ถ้าไม่มีหมายเหตุ ให้แสดงสีเขียวทั้งหมด
                        restaurantStatus.text = restaurant.status
                        restaurantStatus.setTextColor(greenColor)
                    }
                } else {
                    // ร้านปิด แสดงสีแดงทั้งหมด
                    restaurantStatus.text = restaurant.status
                    restaurantStatus.setTextColor(redColor)
                }
                
                val deliveryInfo = "เวลาทำอาหาร: ${restaurant.deliveryTime} นาที"
                deliveryTimeInfo.text = deliveryInfo
                
                // กำหนด visibility สำหรับคำอธิบายร้านอาหาร
                restaurantDescription.text = restaurant.description
                restaurantDescription.visibility = View.GONE
            }
        }
    }

    private class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem == newItem
        }
    }
} 