package com.nhom2.learnenglish.feature.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nhom2.learnenglish.core.network.RetrofitClient
import com.nhom2.learnenglish.core.network.user.ChangePasswordRequest
import com.nhom2.learnenglish.core.network.user.UserApi
import com.nhom2.learnenglish.core.util.SessionManager
import com.nhom2.learnenglish.databinding.ActivityChangePasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var userApi: UserApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        userApi = RetrofitClient.getInstance().create(UserApi::class.java)

        initViews()
    }

    private fun initViews() {
        this.enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(android.R.id.content),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        binding.btnBack.setOnClickListener { finish() }

        binding.btnChangePassword.setOnClickListener {
            val currentPassword = binding.inputCurrentPassword.text.toString().trim()
            val newPassword = binding.inputNewPassword.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (newPassword.length < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            changePassword(currentPassword, newPassword)
        }
    }

    private fun changePassword(currentPass: String, newPass: String) {
        val tokenStr = sessionManager.fetchAuthToken()
        if (tokenStr.isNullOrEmpty()) return
        val token = "Bearer $tokenStr"
        
        binding.btnChangePassword.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = userApi.changePassword(
                    token = token,
                    request = ChangePasswordRequest(currentPass, newPass)
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ChangePasswordActivity, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ChangePasswordActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnChangePassword.isEnabled = true
                }
            }
        }
    }
}
