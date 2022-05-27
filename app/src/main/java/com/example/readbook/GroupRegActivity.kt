package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Toast
import com.example.readbook.databinding.ActivityGroupRegBinding
import com.example.readbook.model.GroupChatModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class GroupRegActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGroupRegBinding
    private var groupChatModel = GroupChatModel()
    private var comment = GroupChatModel.Comment(null, null, null)
    private var uid : String? = null
    val time = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
    val curTime = dateFormat.format(Date(time)).toString()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_reg_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        R.id.toolbar_reg_button -> {
            uid = Firebase.auth.currentUser?.uid.toString()



            var gId = FirebaseDatabase.getInstance().getReference("groupChatrooms").push().key


            groupChatModel.users.toString()
            groupChatModel.groupName=binding.edGName.text.toString()
            groupChatModel.groupDes=binding.edGDes.text.toString()
            groupChatModel.userLimit=binding.numberPicker.value
            groupChatModel.chief= uid.toString()
            groupChatModel.groupId=gId.toString()


            comment=GroupChatModel.Comment(uid, "독서모임에 오신 것을 환영합니다!",curTime)


            if (binding.edGName.text.isEmpty() || binding.edGDes.text.isEmpty()) {
                Toast.makeText(this, "모든 항목을 작성해주세요.", Toast.LENGTH_SHORT)
                    .show()
                Log.d("shinshin", "${gId}")
                Log.d("shin", "${FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId.toString()).setValue(groupChatModel)}")
            } else {
                FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId.toString()).setValue(groupChatModel)

                FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId.toString()).addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId.toString()).child("comments").push().setValue(comment)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

                Toast.makeText(this, "모임이 등록되었습니다.", Toast.LENGTH_SHORT)
                    .show()
                Log.d("shinshin", "${gId}")
                Log.d("shin", "${FirebaseDatabase.getInstance().getReference("groupChatrooms").child(gId.toString()).setValue(groupChatModel)}")
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