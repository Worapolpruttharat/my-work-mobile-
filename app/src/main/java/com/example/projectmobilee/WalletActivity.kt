package com.example.projectmobilee

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobilee.databinding.ActivityWalletBinding
import com.example.projectmobilee.manager.WalletManager

class WalletActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletBinding
    private lateinit var walletManager: WalletManager
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.uploadSlipButton.text = "สลิปที่เลือก ✓"
                binding.confirmButton.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "เติมเงิน"

        walletManager = WalletManager.getInstance(this)
        updateBalance()
        setupButtons()
    }

    private fun updateBalance() {
        binding.balanceText.text = "฿%.2f".format(walletManager.getBalance())
    }

    private fun setupButtons() {
        binding.scbButton.setOnClickListener {
            // แสดงเลขบัญชี SCB
            binding.qrCodeImage.visibility = View.GONE
            binding.accountNumberText.text = "123-456-7890"
            binding.accountNumberText.visibility = View.VISIBLE
            binding.confirmButton.visibility = View.GONE
        }

        binding.promptpayButton.setOnClickListener {
            // แสดง QR Code
            binding.accountNumberText.visibility = View.GONE
            binding.qrCodeImage.visibility = View.VISIBLE
            binding.confirmButton.visibility = View.GONE
        }

        binding.uploadSlipButton.setOnClickListener {
            val amountText = binding.amountInput.text.toString()
            if (amountText.isEmpty()) {
                Toast.makeText(this, "กรุณาระบุจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // แสดงข้อความอัพโหลดสลิปสำเร็จโดยไม่ต้องเลือกรูปจริง
            selectedImageUri = Uri.parse("content://dummy/slip")
            Toast.makeText(this, "อัพโหลดสลิปสำเร็จ", Toast.LENGTH_SHORT).show()
            binding.confirmButton.visibility = View.VISIBLE
        }

        binding.confirmButton.setOnClickListener {
            val amountText = binding.amountInput.text.toString()
            if (amountText.isEmpty()) {
                Toast.makeText(this, "กรุณาระบุจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val amount = amountText.toDouble()
                if (amount <= 0) {
                    Toast.makeText(this, "จำนวนเงินต้องมากกว่า 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // เติมเงินและแสดงข้อความสำเร็จ
                walletManager.addBalance(amount)
                updateBalance()
                
                Toast.makeText(this, "การเติมเงินสำเร็จ", Toast.LENGTH_SHORT).show()
                
                // รีเซ็ตฟอร์ม
                binding.amountInput.text?.clear()
                binding.accountNumberText.visibility = View.GONE
                binding.qrCodeImage.visibility = View.GONE
                binding.confirmButton.visibility = View.GONE
                binding.uploadSlipButton.text = "อัพโหลดสลิป"
                selectedImageUri = null
                
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "จำนวนเงินไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 