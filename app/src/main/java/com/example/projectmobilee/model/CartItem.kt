package com.example.projectmobilee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    var quantity: Int,
    val imageUrl: String? = null,
    val restaurantId: String = "",
    val restaurantName: String = ""
) : Parcelable {
    fun getTotalPrice(): Double {
        return price * quantity
    }
} 