package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hrmapps.R
import com.hrmapps.databinding.ActivitySplashScreenBinding
import com.hrmapps.ui.view.personil.activity.MainActivity
import com.hrmapps.ui.view.staff.activity.StaffMainActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val fakeGPSPackages = listOf(
        "com.incorporateapps.fakegps.fre",  // Fake GPS Free
        "com.lexa.fakegps",                 // Fake GPS Location
        "com.mhh.fakegps",                  // Fake GPS Joystick & Routes
        "com.android.fusedlocation",        // Location Changer
        "com.gpsjoystick",                  // GPS Joystick
        "com.yeager.fakegps"                // Fake GPS Pro
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("userPref", MODE_PRIVATE)

        setupAnimation()

        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.lavLoading.visibility = View.GONE
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 3000)
        } else {
            val token = sharedPreferences.getString("token", null)
            val role = sharedPreferences.getString("role", null)

            if (isInternetAvailable()) {
                if (token != null && role != null) {
                    if (isFakeGPSActive()) {
                        showFakeGPSDialog()
                    } else {
                        navigateAfterDelay(role)
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.lavLoading.visibility = View.GONE
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }, 3000)
                }
            } else {
                showNoInternetMessage()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.lavLoading.visibility = View.GONE
                    finish()
                }, 3000)
            }
        }
    }

    private fun setupAnimation() {
        binding.lavLoading.apply {
            setAnimation(R.raw.loading)
            playAnimation()
            loop(true)
        }
    }

    private fun navigateAfterDelay(role: String) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.lavLoading.visibility = View.GONE

            val nextActivity = when (role) {
                "employee" -> MainActivity::class.java
                "staff" -> StaffMainActivity::class.java
                else -> LoginActivity::class.java
            }
            startActivity(Intent(this, nextActivity))
            finish()
        }, 3000) // Delay 3 detik
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

    private fun isMockLocationEnabled(): Boolean {
        return try {
            Settings.Secure.getInt(contentResolver, Settings.Secure.ALLOW_MOCK_LOCATION) != 0
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    private fun isFakeGPSAppInstalled(): Boolean {
        val pm: PackageManager = packageManager
        for (packageName in fakeGPSPackages) {
            try {
                pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Aplikasi tidak ditemukan
            }
        }
        return false // Tidak ada aplikasi Fake GPS ditemukan
    }

    private fun isFakeGPSActive(): Boolean {
        return isMockLocationEnabled() || isFakeGPSAppInstalled()
    }

    private fun showFakeGPSDialog() {
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Fake GPS or mock location detected. Please remove any location spoofing apps.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }
}
