package com.hrmapps.ui.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.hrmapps.R
import com.hrmapps.databinding.ActivityMainBinding
import com.hrmapps.ui.view.fragment.HistoryFragment
import com.hrmapps.ui.view.fragment.HomeFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

}