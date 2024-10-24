package com.hrmapps.ui.view.activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentDialog
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import com.hrmapps.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressDialog = ProgressDialog(this).apply {
            setMessage("Loading...")
            setCancelable(false)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            progressDialog.show()
            if (validateInput(email, password)) {
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
        } else if (password.length < 6) {
            binding.passwordInputLayout.error = "Password harus minimal 6 karakter"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }
}