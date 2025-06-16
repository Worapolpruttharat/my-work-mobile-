package com.example.projectmobilee

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobilee.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                showLoading(true)
                performLogin(email, password)
            }
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loginButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun performLogin(email: String, password: String) {
        // TODO: ส่งข้อมูลไปยัง API
        // ตัวอย่างการจำลองการส่งข้อมูล
        binding.root.postDelayed({
            try {
                // จำลองการส่งข้อมูลสำเร็จ
                Toast.makeText(this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show()
                // นำทางไปยังหน้าหลัก
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // ปิดหน้า Login
            } catch (e: Exception) {
                Toast.makeText(this, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }, 2000) // รอ 2 วินาที
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailEditText.error = "กรุณากรอกอีเมล"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "รูปแบบอีเมลไม่ถูกต้อง"
            return false
        }
        if (password.isEmpty()) {
            binding.passwordEditText.error = "กรุณากรอกรหัสผ่าน"
            return false
        }
        if (password.length < 6) {
            binding.passwordEditText.error = "รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร"
            return false
        }
        return true
    }
} 