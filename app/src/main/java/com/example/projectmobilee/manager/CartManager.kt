package com.example.projectmobilee.manager

import com.example.projectmobilee.model.CartItem

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        // ตรวจสอบว่ามีอาหารรายการนี้อยู่แล้วหรือไม่
        val existingItem = items.find { it.name == item.name && it.restaurantId == item.restaurantId }
        if (existingItem != null) {
            // ถ้ามีแล้ว ให้เพิ่มจำนวน
            updateItemQuantity(existingItem, existingItem.quantity + item.quantity)
        } else {
            // ถ้ายังไม่มี ให้เพิ่มรายการใหม่
            items.add(item)
        }
    }

    fun updateItemQuantity(item: CartItem, newQuantity: Int) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item.copy(quantity = newQuantity)
        }
    }

    fun removeItem(item: CartItem) {
        items.remove(item)
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getItemsByRestaurant(): Map<String, List<CartItem>> {
        return items.groupBy { it.restaurantName }
    }

    fun clearCart() {
        items.clear()
    }

    fun getTotalPrice(): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun isEmpty(): Boolean = items.isEmpty()
} 