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
import com.nhom2.learnenglish.core.data.local.AppDatabase
import com.nhom2.learnenglish.core.network.RetrofitClient
import com.nhom2.learnenglish.core.network.user.ChangePasswordRequest
import com.nhom2.learnenglish.core.network.user.UpdateProfileRequest
import com.nhom2.learnenglish.core.network.user.UserApi
import com.nhom2.learnenglish.core.util.SessionManager
import com.nhom2.learnenglish.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var userApi: UserApi
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        database = AppDatabase.getInstance(this)
        userApi = RetrofitClient.getInstance().create(UserApi::class.java)

        initViews()
        loadLocalData()
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

        binding.btnSaveProfile.setOnClickListener {
            val fullName = binding.inputFullname.text.toString().trim()
            val avatarUrl = binding.inputAvatarUrl.text.toString().trim()
            if (fullName.isEmpty()) {
                Toast.makeText(this, "Full name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateProfile(fullName, avatarUrl)
        }
    }

    private fun loadLocalData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userId = sessionManager.getCurrentUserId()
            if (userId != null) {
                val user = database.userDao().getByUserId(userId)
                withContext(Dispatchers.Main) {
                    user?.let {
                        binding.inputFullname.setText(it.fullName)
                        binding.inputAvatarUrl.setText(it.avatarUrl ?: "")
                    }
                }
            }
        }
    }

    private fun updateProfile(fullName: String, avatarUrl: String) {
        val tokenStr = sessionManager.fetchAuthToken()
        if (tokenStr.isNullOrEmpty()) return
        val token = "Bearer $tokenStr"
        
        binding.btnSaveProfile.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = userApi.updateProfile(
                    token = token,
                    request = UpdateProfileRequest(fullName, avatarUrl)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val userId = sessionManager.getCurrentUserId()
                        if (userId != null) {
                            val user = database.userDao().getByUserId(userId)
                            if (user != null) {
                                val updatedUser = user.copy(
                                    fullName = body.fullName,
                                    avatarUrl = body.avatarUrl
                                )
                                database.userDao().update(updatedUser)
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditProfileActivity, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditProfileActivity, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditProfileActivity, "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    binding.btnSaveProfile.isEnabled = true
                }
            }
        }
    }
}
