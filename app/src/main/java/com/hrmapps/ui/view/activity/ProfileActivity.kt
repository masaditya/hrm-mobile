package com.hrmapps.ui.view.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.AuthRepository
import com.hrmapps.databinding.ActivityProfileBinding
import com.hrmapps.ui.viewmodel.AuthViewModel
import com.hrmapps.ui.viewmodel.AuthViewModelFactory

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var authViewModel: AuthViewModel

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

        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)
        val apiService = RetrofitBuilder.apiService
        val authRepository = AuthRepository(apiService)

        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))[AuthViewModel::class.java]
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editProfile -> {
                    val intent = Intent(this, EditProfileActivity::class.java)
                    startActivity(intent)
                    true
                    }
                else -> false
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
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
        observerViewModel()
    }

    private fun observerViewModel(){
        authViewModel.logoutResult.observe(this, Observer { result ->
            when {
                result.isSuccess -> {
                    sharedPreferences.edit().clear().apply()
                    sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                result.isFailure -> {
                    Toast.makeText(this, "Logout failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}