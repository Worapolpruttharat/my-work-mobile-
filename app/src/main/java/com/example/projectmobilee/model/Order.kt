package com.example.projectmobilee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val orderDate: String,
    val deliveryTime: String,
    val status: OrderStatus
) : Parcelable 