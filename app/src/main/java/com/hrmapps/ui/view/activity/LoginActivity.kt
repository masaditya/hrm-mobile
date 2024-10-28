package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.AuthRepository
import com.hrmapps.databinding.ActivityLoginBinding
import com.hrmapps.ui.viewmodel.AuthViewModel
import com.hrmapps.ui.viewmodel.AuthViewModelFactory
import com.hrmapps.utils.getAndroidId

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authViewModel: AuthViewModel
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)

        val apiService = RetrofitBuilder.apiService
        val authRepository = AuthRepository(apiService)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]

        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            setCancelable(false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val androidId = getAndroidId(this)
            if (validateInput(email, password)) {
                progressDialog.show()
                authViewModel.login(email, password, "22-666666-666666-1")
            }
        }
        authViewModel.loginResponse.observe(this, Observer { response ->
            progressDialog.dismiss()
            Log.d("LoginResponse", "Response: $response")
            if (response != null && response.message == "Login successful") {
                startActivity(Intent(this, MainActivity::class.java))
                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                sharedPreferences.edit().putString("token", response.token).apply()
                sharedPreferences.edit().putString("companyId", response.user.company_id.toString()).apply()
                sharedPreferences.edit().putString("userId", response.user.id.toString()).apply()
                sharedPreferences.edit().putString("name", response.user.name).apply()
                sharedPreferences.edit().putString("email", response.user.email).apply()
                sharedPreferences.edit().putString("registerIp", response.user.register_ip).apply()

                finish()
                Toast.makeText(this, "Login sukses! Token: ${response.token}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login gagal, periksa kredensial Anda", Toast.LENGTH_SHORT).show()
            }
        })
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Forgot Password diklik", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(email)) {
            binding.emailInputLayout.error = "Email harus diisi"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Format email tidak valid"
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordInputLayout.error = "Password harus diisi"
            isValid = false
        } else if (password.length < 8) {
            binding.passwordInputLayout.error = "Password harus minimal 8 karakter"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }


}
