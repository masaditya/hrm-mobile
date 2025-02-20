package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.auth.AuthRepository
import com.hrmapps.databinding.ActivityLoginBinding
import com.hrmapps.ui.view.personil.activity.MainActivity
import com.hrmapps.ui.view.staff.activity.StaffMainActivity
import com.hrmapps.ui.viewmodel.auth.AuthViewModel
import com.hrmapps.ui.viewmodel.auth.AuthViewModelFactory
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

        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)

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
        authViewModel.loginResponse.observe(this) { response ->
            progressDialog.dismiss()
            if (response != null && response.message == "Login successful") {
                when (response.user.role_name) {
                    "employee" -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        sharedPreferences.edit().putString("role", "employee").apply()
                        sharedPreferences.edit().putString("token", response.token).apply()
                        sharedPreferences.edit().putInt("userId", response.user.id).apply()
                        sharedPreferences.edit().putInt("companyId", response.user.company_id).apply()
                        finish()
                    }
                    "staff" -> {
                        startActivity(Intent(this, StaffMainActivity::class.java))
                        sharedPreferences.edit().putString("role", "staff").apply()
                        sharedPreferences.edit().putString("token", response.token).apply()
                        sharedPreferences.edit().putInt("userId", response.user.id).apply()
                        sharedPreferences.edit().putInt("companyId", response.user.company_id).apply()
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, "Role tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }
        authViewModel.errorMessage.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                progressDialog.dismiss()
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }
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
