package com.example.readbook

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.databinding.ActivityReadlistBinding
import com.example.readbook.model.BookNote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_detail.*
import kotlinx.android.synthetic.main.activity_readlist.*


class ReadListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadlistBinding
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private lateinit var auth: FirebaseAuth
    private var recyclerView: RecyclerView? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.readlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            val intent = Intent(this, ReadListActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        R.id.toolbar_readlist_button -> {
            val intent = Intent(this, Save_deleteActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.topBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth
        val spEmail = Firebase.auth.currentUser?.email.toString()
        val plusEmail = spEmail.replace(".", "+")
        recyclerView = findViewById(R.id.rv_booknote)

        val uid = Firebase.auth.currentUser?.uid.toString()

//        buttonregister.setOnClickListener {
//            val intent = Intent(this, Save_deleteActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        val layoutManager = LinearLayoutManager(this@ReadListActivity)
        binding.rvBooknote.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding.rvBooknote.layoutManager=layoutManager
        binding.rvBooknote.adapter=RecyclerViewAdapter()
        Log.d("pgm" , "intro....................")
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }
    private fun reload() {
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.BookNoteViewHolder>() {
        val user = Firebase.auth.currentUser
        //currentUser = 로그인한 사용자
        val userId = user?.uid
        val spEmail = Firebase.auth.currentUser?.email.toString()
        val userIdSt = userId.toString()
        val plusEmail = spEmail.replace(".", "+")

        val booknotelist = ArrayList<BookNote>()
        init {
            fireDatabase.child("bookdiary").child("${plusEmail}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        booknotelist.clear()
                        Log.d("pgm", "check....................")
                        for (data in snapshot.children) {
                            Log.d("pgm", "${data}")
                            Log.d("pgm", "${booknotelist}")
                            booknotelist.add(data.getValue<BookNote>()!!)
                            Log.d("pgm", "${data.value}")
                            println(data)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookNoteViewHolder {
            Log.d("pgm","${plusEmail}123")
            return BookNoteViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
            )
        }

        inner class BookNoteViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {

            val textView_title: TextView = itemView.findViewById(R.id.rc_booktitle)
            val textView_content: TextView = itemView.findViewById(R.id.rc_bookcontent)
        }

        override fun onBindViewHolder(holder: BookNoteViewHolder, position:Int) {
            /*val spEmail = Firebase.auth.currentUser?.email.toString()
            val plusEmail = spEmail.replace(".", "+").toString()*/

            holder.textView_title.text = booknotelist[position].booktitle.toString()
            holder.textView_content.text = booknotelist[position].bookcontent.toString()
            holder.itemView.setOnClickListener{
                val intent = Intent(holder.itemView?.context, ListDetailActivity::class.java)
                intent.putExtra("title", booknotelist[position].booktitle)
                intent.putExtra("content", booknotelist[position].bookcontent)
                intent.putExtra("bookid",booknotelist[position].bookid)
                intent.putExtra("uid",booknotelist[position].uid)
                Log.d("intent" , "${booknotelist[position].booktitle}")
                Log.d("intent" , "${booknotelist[position].bookcontent}")
                Log.d("booknotelist","${booknotelist[position]}")
                Log.d("bookidtest", "${booknotelist[position].bookid}")


                ContextCompat.startActivity(holder.itemView.context, intent, null)

            }
        }

        override fun getItemCount(): Int {
            Log.d("pgm" , "${booknotelist.size}")
            Log.d("pgm","${plusEmail}")
            return booknotelist.size
        }
    }
}