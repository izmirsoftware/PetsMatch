package com.izmirsoftware.petsmatch.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.izmirsoftware.petsmatch.R
import com.izmirsoftware.petsmatch.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //host navigation fragmente erişiyoruz
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment?
        val navControl = navHostFragment?.navController

        navControl?.let {
            //bottom navigatiton ile host navigation fragment bağlantısı yapıldı
            //çalışması için bottom menü item idleri ile fragment idleri aynı olması lazım
            NavigationUI.setupWithNavController(
                binding.mainBottomNavigation,
                navControl
            )

            //Bottom Navigation item'leri tekrar seçildiğinde sayfayı yenilemesi için eklendi
            binding.mainBottomNavigation.setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> navControl.navigate(R.id.action_global_navigation_home)
                    R.id.navigation_search -> navControl.navigate(R.id.action_global_navigation_search)
                    R.id.navigation_profile -> navControl.navigate(R.id.action_global_navigation_profile)
                }
            }
        }
    }
}