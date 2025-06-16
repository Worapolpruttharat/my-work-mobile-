package com.example.projectmobilee

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobilee.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                // แสดง Progress Bar
                binding.registerButton.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE

                // TODO: ส่งข้อมูลไปยัง API
                // ตัวอย่างการจำลองการส่งข้อมูล
                binding.root.postDelayed({
                    // จำลองการส่งข้อมูลสำเร็จ
                    Toast.makeText(this, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show()
                    finish() // กลับไปหน้า Login
                }, 2000) // รอ 2 วินาที
            }
        }

        binding.backToLoginButton.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            binding.nameEditText.error = "กรุณากรอกชื่อ-นามสกุล"
            return false
        }
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
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordEditText.error = "กรุณายืนยันรหัสผ่าน"
            return false
        }
        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "รหัสผ่านไม่ตรงกัน"
            return false
        }
        return true
    }
} 