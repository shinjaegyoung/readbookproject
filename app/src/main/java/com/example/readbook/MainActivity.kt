package com.example.readbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.example.readbook.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*

private lateinit var auth: FirebaseAuth

private lateinit var homeFragment: HomeFragment
private lateinit var chatFragment: ChatFragment
private lateinit var marketFragment: MarketFragment
private lateinit var profileFragment: ProfileFragment
private lateinit var groupListFragemnt:GroupListFragment

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

        bottom_nav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)


            homeFragment = HomeFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homeFragment).commit()





        //setSupportActionBar(findViewById(R.id.marketfragment_toolbar))
        //getSupportActionBar().setTitle("도서 거래")

    }

    private val BottomNavItemSelectedListener =BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {

            R.id.menu_home -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager . beginTransaction ().replace(
                    R.id.fragments_frame,
                    homeFragment
                ).commit()
            }
            R.id.menu_groupList -> {
                groupListFragemnt = GroupListFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragments_frame,
                    groupListFragemnt
                ).commit()
            }
            R.id.menu_chat -> {
                chatFragment = ChatFragment.newInstance()
                supportFragmentManager.beginTransaction ().replace(
                    R.id.fragments_frame,
                    chatFragment
                ).commit()
            }
            R.id.menu_market -> {
                marketFragment = MarketFragment.newInstance()
                supportFragmentManager.beginTransaction ().replace(
                    R.id.fragments_frame,
                    marketFragment
                ).commit()
            }
            R.id.menu_profile -> {
                profileFragment = ProfileFragment.newInstance()
                supportFragmentManager.beginTransaction ().replace(R.id.fragments_frame, profileFragment)
                    .commit()
            }
        }
        true
    }


}