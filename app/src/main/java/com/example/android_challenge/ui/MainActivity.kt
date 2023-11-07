package com.example.android_challenge.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android_challenge.R
import com.example.android_challenge.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener {_, destination, _ ->
            if(
                destination.id == R.id.login_fragment ||
                destination.id == R.id.transaction_fragment ||
                destination.id == R.id.camera_fragment ||
                destination.id == R.id.result_scanner_fragment
            ){
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
            navView.setupWithNavController(navController)
        }
    }

}