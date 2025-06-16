package com.example.projectmobilee

object WalletManager {
    private var balance: Float = 0f

    fun getCurrentBalance(): Float {
        return balance
    }

    fun addBalance(amount: Float) {
        balance += amount
    }

    fun deductBalance(amount: Float): Boolean {
        if (balance >= amount) {
            balance -= amount
            return true
        }
        return false
    }
} 