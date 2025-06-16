package com.example.projectmobilee.model

data class MenuItem(
    val name: String,
    val description: String,
    val price: Double,
    val isOutOfStock: Boolean = false
) 