package com.hrmapps.ui.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hrmapps.R
import com.hrmapps.databinding.ActivitySplashScreenBinding

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
        val token = sharedPreferences.getString("token", "")
        Log.d("SplashScreenActivity", "Token: $token")
        Handler().postDelayed({
            if (isLoggedIn) {
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                binding.progressBar.visibility = View.GONE
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 3000)

    }
}