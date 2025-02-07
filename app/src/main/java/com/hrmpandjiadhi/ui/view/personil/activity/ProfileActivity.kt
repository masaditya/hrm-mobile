package com.hrmpandjiadhi.ui.view.personil.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.hrmpandjiadhi.BuildConfig
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.api.RetrofitBuilder
import com.hrmpandjiadhi.data.repository.auth.AuthRepository
import com.hrmpandjiadhi.data.repository.auth.GetUserRepository
import com.hrmpandjiadhi.databinding.ActivityProfileBinding
import com.hrmpandjiadhi.ui.view.activity.LoginActivity
import com.hrmpandjiadhi.ui.viewmodel.auth.AuthViewModel
import com.hrmpandjiadhi.ui.viewmodel.auth.AuthViewModelFactory
import com.hrmpandjiadhi.ui.viewmodel.auth.GetUserLoginViewModel
import com.hrmpandjiadhi.ui.viewmodel.auth.GetUserLoginViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authViewModel: AuthViewModel
    private lateinit var viewModel: GetUserLoginViewModel
    private lateinit var viewModelFactory: GetUserLoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)
        val apiService = RetrofitBuilder.apiService
        val authRepository = AuthRepository(apiService)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]


        val getUserRepository = GetUserRepository(apiService)
        viewModelFactory = GetUserLoginViewModelFactory(getUserRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GetUserLoginViewModel::class.java]

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.buttonLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                val token = sharedPreferences.getString("token", null)
                if (token != null) {
                    authViewModel.logout(token)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

            builder.create().show()


        }
        binding.buttonChangeEmail.setOnClickListener {
            val intent = Intent(this, ChangeEmailActivity::class.java)
            startActivity(intent)
        }
        binding.buttonChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        setupUI()
        observerViewModel()
        displayVersionName()
    }

    private fun observerViewModel(){

        authViewModel.logoutResult.observe(this) { result ->
            when {
                result.isSuccess -> {
                    sharedPreferences.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                result.isFailure -> {
                    Toast.makeText(this, "Logout failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        authViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }

        authViewModel.errorMessage.observe(this){  errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupUI() {
        val token = sharedPreferences.getString("token", "").toString()

        viewModel.getUserLogin(token)

        viewModel.userResponse.observe(this) { response ->
            val user = response.data
            binding.tvName.text = user.name
            binding.tvEmail.text = user.email
            binding.etCompany.setText(user.designation)
            binding.etRole.setText(user.role)
            binding.etEmployeeId.setText(user.employee_id)
            Glide.with(this)
                .load(BuildConfig.img_url + response.data.image)
                .placeholder(R.drawable.placeholeder)
                .into(binding.profileImage)
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingBar.visibility = View.VISIBLE
            } else {
                binding.loadingBar.visibility = View.GONE
            }
        }


    }
    private fun displayVersionName() {
        try {
            val versionName = packageManager.getPackageInfo(packageName, 0).versionName
            binding.tvVersion.text = getString(R.string.version_name, versionName)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvVersion.text = getString(R.string.version_name_error)
        }
    }
}