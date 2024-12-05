package com.hrmapps.ui.view.personil.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.auth.AuthRepository
import com.hrmapps.databinding.ActivityChangePasswordBinding
import com.hrmapps.ui.viewmodel.auth.AuthViewModel
import com.hrmapps.ui.viewmodel.auth.AuthViewModelFactory

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        val apiService = RetrofitBuilder.apiService
        val repository = AuthRepository(apiService)
        val factory = AuthViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        observeViewModel()

        binding.btnSave.setOnClickListener {
            val token = sharedPreferences.getString("token", "") ?: ""
            val password = binding.newPassword.text.toString()
            val passwordConfirmation = binding.confirmPassword.text.toString()

            if (password.isEmpty()) {
                binding.newPassword.error = "Password tidak boleh kosong"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }
            if (passwordConfirmation.isEmpty()) {
                binding.confirmPassword.error = "Konfirmasi password tidak boleh kosong"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }
            if (password != passwordConfirmation) {
                binding.confirmPassword.error = "Password tidak sama"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 8) {
                binding.newPassword.error = "Password minimal 8 karakter"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }
            if (passwordConfirmation.length < 8) {
                binding.confirmPassword.error = "Password minimal 8 karakter"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }

            viewModel.changePassword(token, password, passwordConfirmation)
        }
    }
    private fun observeViewModel(){
        viewModel.changePasswordResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                binding.newPassword.text?.clear()
                binding.confirmPassword.text?.clear()
            } else {
                Toast.makeText(this, "Gagal mengubah password", Toast.LENGTH_SHORT).show()
                binding.newPassword.text?.clear()
                binding.confirmPassword.text?.clear()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

    }
}