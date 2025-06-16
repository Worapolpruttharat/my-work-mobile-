package com.example.projectmobilee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val status: String,
    val phone: String,
    val rating: Float,
    val deliveryTime: Int,
    val minOrder: Double,
    val deliveryFee: Double,
    val imageResId: Int = 0
) : Parcelable 