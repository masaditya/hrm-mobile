package com.hrmpandjiadhi.ui.view.staff.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hrmpandjiadhi.BuildConfig
import com.hrmpandjiadhi.R
import com.hrmpandjiadhi.data.api.RetrofitBuilder
import com.hrmpandjiadhi.data.repository.auth.GetUserRepository
import com.hrmpandjiadhi.databinding.ActivityStaffMainBinding
import com.hrmpandjiadhi.ui.view.activity.LoginActivity
import com.hrmpandjiadhi.ui.view.fragment.HistoryFragment
import com.hrmpandjiadhi.ui.view.fragment.NoticeFragment
import com.hrmpandjiadhi.ui.view.staff.fragment.HomeStaffFragment
import com.hrmpandjiadhi.ui.viewmodel.auth.GetUserLoginViewModel
import com.hrmpandjiadhi.ui.viewmodel.auth.GetUserLoginViewModelFactory
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StaffMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: GetUserLoginViewModel
    private lateinit var viewModelFactory: GetUserLoginViewModelFactory

    companion object{
        private val LOCATION_PERMISSION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStaffMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!isInternetAvailable()) {
            val snackbar = Snackbar.make(binding.root, "No internet connection. Please check your network and try again.", Snackbar.LENGTH_LONG)
            snackbar.setAction("Retry") {
                if (isInternetAvailable()) {
                    snackbar.dismiss()
                    setupUI()
                } else {
                    snackbar.setText("Still no internet. Please check your network.")
                    snackbar.show()
                }
            }
            snackbar.show()
            return
        }


        checkLocationPermission()

        val apiService = RetrofitBuilder.apiService
        val getUserRepository = GetUserRepository(apiService)
        viewModelFactory = GetUserLoginViewModelFactory(getUserRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GetUserLoginViewModel::class.java]

        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)

        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(1, "History", R.drawable.ic_history)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(2, "Home", R.drawable.ic_home)
        )
        binding.bottomNavigation.add(
            CurvedBottomNavigation.Model(3, "Notifications", R.drawable.ic_notifications)
        )

        binding.bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    loadFragment(HistoryFragment())
                }

                2 -> {
                    loadFragment(HomeStaffFragment())
                }

                3 -> {
                    loadFragment(NoticeFragment())
                }
            }
        }

        binding.bottomNavigation.show(2)
        loadFragment(HomeStaffFragment())

        binding.civUser.setOnClickListener {
            startActivity(Intent(this, ProfileStaffActivity::class.java))
        }
        binding.llProfile.setOnClickListener {
            startActivity(Intent(this, ProfileStaffActivity::class.java))
        }
        setupUI()

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
    private fun setupUI() {
        val token = sharedPreferences.getString("token", "").toString()

        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            sharedPreferences.edit().clear().apply()
            startActivity(intent)
            finish()
            return
        }

        viewModel.getUserLogin(token)

        viewModel.userResponse.observe(this) { response ->
            val user = response.data
            binding.tvUserName.text = user.name
            binding.tvEmail.text = user.email
            Glide.with(this)
                .load(BuildConfig.img_url + response.data.image)
                .placeholder(R.drawable.placeholeder)
                .into(binding.civUser)

        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()){
                Toast.makeText(this, errorMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.shimmerLayoutProfile.startShimmer()
                binding.layoutProfile.visibility = View.GONE
                binding.shimmerLayoutProfile.visibility = View.VISIBLE
            } else {
                lifecycleScope.launch {
                    delay(1000)
                    binding.shimmerLayoutProfile.stopShimmer()
                    binding.shimmerLayoutProfile.visibility = View.GONE
                    binding.layoutProfile.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                StaffMainActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun hasLocationPermission() = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}