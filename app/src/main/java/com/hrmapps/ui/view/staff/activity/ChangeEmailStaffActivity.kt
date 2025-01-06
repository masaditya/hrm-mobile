package com.hrmapps.ui.view.staff.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import android.content.SharedPreferences
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.auth.AuthRepository
import com.hrmapps.databinding.ActivityChangeEmailStaffBinding
import com.hrmapps.ui.viewmodel.auth.AuthViewModel
import com.hrmapps.ui.viewmodel.auth.AuthViewModelFactory

class ChangeEmailStaffActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeEmailStaffBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeEmailStaffBinding.inflate(layoutInflater)
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

        binding.buttonSave.setOnClickListener {
            val token = sharedPreferences.getString("token", "") ?: ""
            val userId = sharedPreferences.getInt("userId", 0)
            val newEmail = binding.etEmail.text.toString()
            if (newEmail.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                binding.etEmail.error = "Format email tidak valid"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            viewModel.changeEmail(token,userId, newEmail)
        }
    }
    private fun observeViewModel(){
        viewModel.updateEmailResult.observe(this) { result ->
            if (result.isSuccess) {
                binding.etEmail.text?.clear()
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                binding.etEmail.error = errorMessage
                binding.etEmail.requestFocus()
                binding.etEmail.text?.clear()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = VISIBLE
            } else {
                binding.loadingBar.visibility = GONE
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }

    }

}