package com.example.projectmobilee

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class OrderStatus : Parcelable {
    PENDING,
    PROCESSING,
    COMPLETED
} 