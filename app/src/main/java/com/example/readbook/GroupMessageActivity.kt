package com.example.readbook

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.model.GroupChatModel
import com.example.readbook.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_group_message.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GroupMessageActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var uid : String? = null
    private var recyclerView : RecyclerView? = null
    private var gid : String? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        R.id.toolbar_exit_button -> {
            var chief = intent.getStringExtra("chief")
            uid = Firebase.auth.currentUser?.uid.toString()
            gid = intent.getStringExtra("gId")

            if(uid == chief){
                fireDatabase.child("groupChatrooms").child(gid!!).removeValue()
            }else{
                fireDatabase.child("groupChatrooms").child(gid!!).child("users/$uid").removeValue()
            }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message)

        setSupportActionBar(findViewById(R.id.topBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        gid = intent.getStringExtra("gId").toString()

        val imageView = findViewById<ImageView>(R.id.messageActivity_ImageView)
        val editText = findViewById<TextView>(R.id.messageActivity_editText)

        //메세지를 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.messageActivity_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(this@GroupMessageActivity)
        recyclerView?.adapter = RecyclerViewAdapter()


        imageView?.setOnClickListener {
            val chatModel = GroupChatModel()
            chatModel.users.put(uid.toString(), true)

            val comment = GroupChatModel.Comment(uid, editText.text.toString(), curTime)

            fireDatabase.child("groupChatrooms").child(gid!!).child("comments").push().setValue(comment)
            messageActivity_editText.text = null
        }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<GroupChatModel.Comment>()
        private var user : User? = null
        init{
            gid = intent.getStringExtra("gId").toString()
            fireDatabase.child("groupChatrooms").child(gid!!).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatName.text=snapshot.getValue<GroupChatModel>()?.groupName.toString()
                    var maxNum=snapshot.getValue<GroupChatModel>()?.userLimit.toString()
                    var curNum=snapshot.getValue<GroupChatModel>()?.users?.size.toString()
                    chatLimit.text="(${maxNum}/${curNum})"
                    getMessageList()
                }
            })
        }

        fun getMessageList(){
            fireDatabase.child("groupChatrooms").child(gid!!).child("comments").addValueEventListener(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<GroupChatModel.Comment>()
                        comments.add(item!!)
                        println(comments)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                fireDatabase.child("users").child("${comments[position].uid}").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        user = snapshot.getValue<User>()
                        Glide.with(holder.itemView.context)
                            .load(user?.profileImageUrl)
                            .apply(RequestOptions().circleCrop())
                            .into(holder.imageView_profile)
                        holder.textView_name.text = user?.name
                        holder.layout_destination.visibility = View.VISIBLE
                        holder.textView_name.visibility = View.VISIBLE
                        holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                        holder.layout_main.gravity = Gravity.LEFT
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time : TextView = view.findViewById(R.id.messageItem_textView_time)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
}