package com.example.projectmobilee.manager

import android.content.Context
import android.content.SharedPreferences

class WalletManager private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "wallet_preferences"
        private const val KEY_BALANCE = "balance"
        private var instance: WalletManager? = null

        fun getInstance(context: Context): WalletManager {
            return instance ?: synchronized(this) {
                instance ?: WalletManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getBalance(): Double {
        return sharedPreferences.getFloat(KEY_BALANCE, 0f).toDouble()
    }

    fun addBalance(amount: Double) {
        val currentBalance = getBalance()
        val newBalance = currentBalance + amount
        sharedPreferences.edit().putFloat(KEY_BALANCE, newBalance.toFloat()).apply()
    }

    fun deductBalance(amount: Double): Boolean {
        val currentBalance = getBalance()
        if (currentBalance >= amount) {
            val newBalance = currentBalance - amount
            sharedPreferences.edit().putFloat(KEY_BALANCE, newBalance.toFloat()).apply()
            return true
        }
        return false
    }

    fun hasEnoughBalance(amount: Double): Boolean {
        return getBalance() >= amount
    }
} 