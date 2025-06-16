package com.example.projectmobilee

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val foodName: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String? = null
) : Parcelable 