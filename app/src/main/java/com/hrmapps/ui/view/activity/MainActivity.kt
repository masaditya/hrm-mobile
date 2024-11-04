package com.hrmapps.ui.view.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.hrmapps.BuildConfig
import com.hrmapps.R
import com.hrmapps.data.api.RetrofitBuilder
import com.hrmapps.data.repository.AuthRepository
import com.hrmapps.data.repository.GetUserRepository
import com.hrmapps.databinding.ActivityMainBinding
import com.hrmapps.ui.view.fragment.HistoryFragment
import com.hrmapps.ui.view.fragment.HomeFragment
import com.hrmapps.ui.viewmodel.AuthViewModel
import com.hrmapps.ui.viewmodel.AuthViewModelFactory
import com.hrmapps.ui.viewmodel.CheckInStatusViewModel
import com.hrmapps.ui.viewmodel.GetUserLoginViewModel
import com.hrmapps.ui.viewmodel.GetUserLoginViewModelFactory
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val CAMERA_PERMISSION_CODE = 100
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: GetUserLoginViewModel
    private lateinit var viewModelFactory: GetUserLoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.primary_dark_second)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val apiService = RetrofitBuilder.apiService
        val getUserRepository = GetUserRepository(apiService)
        viewModelFactory = GetUserLoginViewModelFactory(getUserRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[GetUserLoginViewModel::class.java]

        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)

        checkCameraPermission()

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
                    loadFragment(HomeFragment())
                }

                3 -> {
                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.bottomNavigation.show(2)
        loadFragment(HomeFragment())

        binding.civUser.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.llProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
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

        viewModel.getUserLogin(token)

        viewModel.userResponse.observe(this) { response ->
            val user = response.data
            binding.tvUserName.text = user.name
            binding.tvEmail.text = user.email
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.GONE
            } else {
                binding.shimmerLayout.stopShimmer()
                binding.shimmerLayout.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
            }
        }
    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            Log.d("Permission", "Izin kamera diberikan")

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Izin kamera diberikan")

            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}