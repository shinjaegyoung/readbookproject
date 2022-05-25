package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.databinding.ActivityMyProductBinding
import com.example.readbook.fragment.MyProductChatFragment
import com.example.readbook.fragment.MyProductItemFragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MyProductActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMyProductBinding
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private var recyclerView: RecyclerView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.topBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().add(R.id.tabContent, MyProductItemFragment()).commit()

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction= supportFragmentManager.beginTransaction()
                when(tab?.text){
                    "내 상품" -> transaction.replace(R.id.tabContent, MyProductItemFragment())
                    "내 거래" -> transaction.replace(R.id.tabContent, MyProductChatFragment())
                }
                transaction.addToBackStack(null)
                transaction.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

    }
}