package com.example.agrofusion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import com.example.agrofusion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val communityFragment = CommunityFragment()
    private val myGardenFragment = MyGardenFragment()
    private val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(homeFragment)

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId) {
                R.id.menu_home -> replaceFragment(homeFragment)
                R.id.menu_community -> replaceFragment(communityFragment)
                R.id.menu_my_garden -> replaceFragment(myGardenFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

    }
}