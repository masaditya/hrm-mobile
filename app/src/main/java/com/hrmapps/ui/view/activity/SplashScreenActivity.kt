package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.hrmapps.R
import com.hrmapps.databinding.ActivitySplashScreenBinding
import com.hrmapps.ui.view.personil.activity.MainActivity
import com.hrmapps.ui.view.staff.activity.StaffMainActivity
import com.hrmapps.ui.view.staff.activity.TimeSheetActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("isLoggedIn", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val role = sharedPreferences.getString("role", "")
        setupAnimation()

        if (isInternetAvailable()) {
            if (role != null) {
                navigateAfterDelay(isLoggedIn, role)
            }
        } else {
            showNoInternetMessage()
        }
    }

    private fun setupAnimation() {
        binding.lavLoading.apply {
            setAnimation(R.raw.loading)
            playAnimation()
            loop(true)
        }
    }

    private fun navigateAfterDelay(isLoggedIn: Boolean, role: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.lavLoading.visibility = View.GONE
            val nextActivity =
                if (isLoggedIn && role == "employee")
                    MainActivity::class.java
                else if (isLoggedIn && role == "staff")
                    StaffMainActivity::class.java
                else
                    LoginActivity::class.java
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000)
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun showNoInternetMessage() {
        binding.lavLoading.visibility = View.GONE
        Toast.makeText(this, "No internet connection. Please check your network.", Toast.LENGTH_LONG).show()
    }
}
