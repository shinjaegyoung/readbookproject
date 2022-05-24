package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import com.example.readbook.databinding.ActivityGroupRegBinding
import com.example.readbook.model.GroupChatModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class GroupRegActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGroupRegBinding
    private var groupChatModel = GroupChatModel()
    private var comment = GroupChatModel.Comment(null, null, null)
    private var uid : String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_reg_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.toolbar_reg_button -> {
            uid = Firebase.auth.currentUser?.uid.toString()
            val gId = FirebaseDatabase.getInstance().getReference("groupChatrooms").push().toString()

            groupChatModel.users.put(uid.toString(), true)
            groupChatModel.groupName=binding.edGName.text.toString()
            groupChatModel.groupDes=binding.edGDes.text.toString()
            groupChatModel.userLimit=binding.numberPicker.value
            groupChatModel.groupId=gId

            if (binding.edGName.text.isEmpty() || binding.edGDes.text.isEmpty()) {
                Toast.makeText(this, "모든 항목을 작성해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId).setValue(groupChatModel)
                Toast.makeText(this, "모임이 등록되었습니다.", Toast.LENGTH_SHORT)
                    .show()

                // 목록으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
            true
        }
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGroupRegBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.numberPicker.minValue = 2
        binding.numberPicker.maxValue = 10
        binding.numberPicker.wrapSelectorWheel = false
        binding.numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}