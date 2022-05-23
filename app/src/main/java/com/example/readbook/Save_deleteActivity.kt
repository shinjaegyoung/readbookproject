package com.example.readbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.example.readbook.databinding.ActivitySaveDeleteBinding
import com.example.readbook.model.BookNote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var auth: FirebaseAuth
class Save_deleteActivity : AppCompatActivity() {
    private val BookNote = BookNote()
    val useremail =  Firebase.auth.currentUser?.email.toString()
    var useremail_plus = useremail.replace(".", "+")
    val user = Firebase.auth.currentUser
    val fireDatabase = FirebaseDatabase.getInstance()
    val new2Ref = fireDatabase.getReference("bookdiray").child(useremail_plus).push()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.readlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            true
        }
        R.id.toolbar_readlist_button -> {
                BookNote.bookid = new2Ref.key.toString()
            val booktitle = findViewById<EditText>(R.id.booktitle)
            val bookcontent = findViewById<EditText>(R.id.bookcontent)
            //val binding=ActivitySaveDeleteBinding.inflate(layoutInflater)
            var booknote = BookNote(
                booktitle.text.toString(),
                bookcontent.text.toString(),
                user?.uid,
                new2Ref.key.toString()
            )
            FirebaseDatabase.getInstance().getReference("bookdiary")
                .child("${useremail_plus}").child(BookNote.bookid.toString()).setValue(booknote)

            val intent = Intent(this, ReadListActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivitySaveDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.topBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth
        database = Firebase.database.reference

        val useremail =  Firebase.auth.currentUser?.email.toString()
        val booktitle = findViewById<EditText>(R.id.booktitle).text
        val bookcontent = findViewById<EditText>(R.id.bookcontent).text
        val user = Firebase.auth.currentUser

        //currentUser = 로그인한 사용자
        val userId = user?.uid
        val userIdSt = userId.toString()
        var useremailarr = useremail.split(".")
        var useremail1 = ""

        for(i in 0 .. useremailarr.size-1 step(1)){
            useremail1 += useremailarr.get(i)
        }

        var useremail_plus = useremail.replace(".", "+")

        BookNote.bookid = new2Ref.key.toString()

//        binding.btnsave.setOnClickListener {
//            val booknote = BookNote(
//                binding.booktitle.text.toString(),
//                binding.bookcontent.text.toString(),
//                userId,
//                new2Ref.key.toString()
//
//            )
//            //database.child("bookdiary").push()
//            // .setValue(booknote)
//            //database.get().=setValue(booknote)
//            //database.child("bookdiary").child("${useremail_plus}").child(BookNote.bookid.toString()).setValue(booknote)
//            FirebaseDatabase.getInstance().getReference("bookdiary")
//                .child("${useremail_plus}").child(BookNote.bookid.toString()).setValue(booknote)
//            /*   intent = Intent(this@ListDetailActivity, ReadListActivity::class.java)
//               startActivity(intent)
//               finish()
//   */
//            val intent = Intent(this@Save_deleteActivity , ReadListActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }


}