package com.hrmapps.ui.view.activity

import android.app.ProgressDialog
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
import com.hrmapps.data.repository.AuthRepository
import com.hrmapps.databinding.ActivityLoginBinding
import com.hrmapps.ui.viewmodel.AuthViewModel
import com.hrmapps.ui.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService = RetrofitBuilder.apiService
        val authRepository = AuthRepository(apiService)
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository)).get(AuthViewModel::class.java)

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

            if (validateInput(email, password)) {
                progressDialog.show()
                performLogin(email, password)
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

    private fun performLogin(email: String, password: String) {
        authViewModel.login(email, password).observe(this) { response ->
            progressDialog.dismiss()
            if (response != null) {
                Toast.makeText(this, "Login sukses! Token: ${response.token}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login gagal, periksa kredensial Anda", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
