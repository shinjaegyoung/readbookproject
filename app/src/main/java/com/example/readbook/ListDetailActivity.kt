package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.set
import com.example.readbook.databinding.ActivityListDetailBinding
import com.example.readbook.fragment.ProfileFragment
import com.example.readbook.model.BookNote
import com.example.readbook.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.activity_readlist.*
import kotlinx.android.synthetic.main.book_item.*

private lateinit var auth: FirebaseAuth
class ListDetailActivity : AppCompatActivity() {

    private val BookNote = BookNote()
    val useremail =  Firebase.auth.currentUser?.email.toString()
    var useremail_plus = useremail.replace(".", "+")
    //currentUser = 로그인한 사용자
    val user = Firebase.auth.currentUser
    val userId = user?.uid
    val fireDatabase = FirebaseDatabase.getInstance()
    val new2Ref = fireDatabase.getReference("bookdiray").child(useremail_plus).push()
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.product_detail_menu, menu)

            return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        R.id.updateProduct -> {
            val booktitle = findViewById<EditText>(R.id.booktitle)
            val bookcontent = findViewById<EditText>(R.id.bookcontent)
            var binding = ActivityListDetailBinding.inflate(layoutInflater)
            setContentView(binding.root)
            BookNote.booktitle = binding.booktitle.text.toString()
            BookNote.bookcontent = binding.bookcontent.text.toString()

           // database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()

            var booknote = BookNote(
                booktitle.text.toString(),
                bookcontent.text.toString(),


                userId.toString(),
                new2Ref.key.toString()
            )

            var t_hashMap = HashMap<String, Any>()
            t_hashMap.put("bookcontent", bookcontent.text.toString())
            t_hashMap.put("booktitle", booktitle.text.toString())
            t_hashMap.put("bookid", intent.getStringExtra("bookid").toString())
            t_hashMap.put("uid", user?.uid.toString())



            //database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").updateChildren(booknote)
            database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").updateChildren(t_hashMap)
            Log.d("sdfsdfsdf", "${FirebaseDatabase.getInstance().getReference("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}")}")


           // database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()
            val intent = Intent(this, ReadListActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        R.id.deleteProduct -> {
            database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()
            Toast.makeText(this,"독서 일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            Log.d("cimera","${database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}")}")
            val intent = Intent(this, ReadListActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        android.R.id.home -> {
            val intent = Intent(this, ReadListActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.booktitle.setText(intent.getStringExtra("title"))
        binding.bookcontent.setText(intent.getStringExtra("content"))

        auth = Firebase.auth
        database = Firebase.database.reference

        val useremail =  Firebase.auth.currentUser?.email.toString()
        val booktitle = findViewById<EditText>(R.id.booktitle).text
        val bookcontent = findViewById<EditText>(R.id.bookcontent).text
        BookNote.bookid = new2Ref.key.toString()
        //currentUser = 로그인한 사용자
        val userId = user?.uid
        val userIdSt = userId.toString()
        var useremailarr = useremail.split(".")
        var useremail1 = ""

        for(i in 0 .. useremailarr.size-1 step(1)){
            useremail1 += useremailarr.get(i)
        }

        var useremail_plus = useremail.replace(".", "+")


//       bookdetaildelete.setOnClickListener {
//            Log.d("keykey11111", "${intent.getStringExtra("bookid")}")
//
//            FirebaseDatabase.getInstance().getReference("bookdiary")
//                .child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()
//
//           val intent = Intent(this@ListDetailActivity , ReadListActivity::class.java)
//           startActivity(intent)
//           finish()
//       }

//        bookdetailupdate?.setOnClickListener{
//            BookNote.booktitle = binding.booktitle.text.toString()
//            BookNote.bookcontent = binding.bookcontent.text.toString()
//
//            database.child("bookdiary").child("${useremail_plus}").child("${intent.getStringExtra("bookid")}").removeValue()
//
//
//            val booknote = BookNote(
//                binding.booktitle.text.toString(),
//                binding.bookcontent.text.toString()
//            )
//
//            database.child("bookdiary").child(useremail_plus).child("${intent.getStringExtra("bookid")}").setValue(booknote)
//
//            val intent = Intent(this@ListDetailActivity , ReadListActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }
}






