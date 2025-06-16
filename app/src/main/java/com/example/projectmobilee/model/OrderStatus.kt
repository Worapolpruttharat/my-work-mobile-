package com.example.projectmobilee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class OrderStatus : Parcelable {
    PENDING,        // รอร้านรับออเดอร์
    ACCEPTED,       // ร้านรับออเดอร์แล้ว
    COOKING,        // กำลังปรุงอาหาร
    READY,          // อาหารพร้อมเสิร์ฟ
    COMPLETED,      // ออเดอร์เสร็จสมบูรณ์
    CANCELLED       // ออเดอร์ถูกยกเลิก
} 