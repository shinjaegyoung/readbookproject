package com.example.readbook.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.GroupRegActivity
import com.example.readbook.MessageActivity
import com.example.readbook.R
import com.example.readbook.databinding.FragmentGroupListBinding
import com.example.readbook.model.GroupChatModel
import com.example.readbook.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class GroupListFragment : Fragment() {
    companion object{
        fun newInstance() : GroupListFragment {
            return GroupListFragment()
        }
    }

    private lateinit var binding : FragmentGroupListBinding
    private lateinit var database: DatabaseReference
    private var groupChatList = ArrayList<GroupChatModel>()
    private var uid : String? = null

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    init {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.groupList_recycler)

        // 자신을 제외하고 users에 저장된 회원들을 가져와 친구 목록으로 작성(삭제할 내용)
        FirebaseDatabase.getInstance().reference.child("groupChatrooms").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("init 부분","success....?")
                groupChatList.clear()
                for(data in snapshot.children){
                    val item = data.getValue<GroupChatModel>()
                    Log.d("init 부분","${data.value}")
                    groupChatList.add(item!!)
                }
                Log.d("init 부분","$groupChatList")
                //this는 액티비티에서 사용가능, 프래그먼트는 requireContext()로 context 가져오기
                recyclerView?.layoutManager = LinearLayoutManager(requireContext())
                recyclerView?.adapter = RecyclerViewAdapter()
            }
        })
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        database = Firebase.database.reference
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)

        // 독서 모임 추가 버튼을 눌러 모임 생성 페이지로 이동
        val regGroup = view?.findViewById<Button>(R.id.btnReg_grouplist)
        regGroup?.setOnClickListener {
            val groupIntent = Intent(context, GroupRegActivity::class.java)
            context?.startActivity(groupIntent)
        }

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_goup_list, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.home_item_iv)
            val textView : TextView = itemView.findViewById(R.id.home_item_tv)
            val textViewEmail : TextView = itemView.findViewById(R.id.home_item_email)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            // 작성된 리스트를 가져와 순서대로 출력(역순으로 고칠 것)
            Log.d("adapter 내부","$groupChatList")
            Glide.with(holder.itemView.context).load(R.drawable.logo2)
                .apply(RequestOptions().circleCrop())
                .override(100,100)
                .into(holder.imageView)
            holder.textView.text = groupChatList[position].groupName
            holder.textViewEmail.text = groupChatList[position].groupDes

            // 아이템을 클릭하면 해당 채팅방으로 이동(uid 비교하여 users에 추가)
            holder.itemView.setOnClickListener{
                uid = Firebase.auth.currentUser?.uid.toString()
                var gid = groupChatList[position].groupId

                val intent = Intent(context, MessageActivity::class.java)
                if(!groupChatList[position].users.contains(uid) && groupChatList[position].userLimit != groupChatList[position].users.size){
                    groupChatList[position].users.put(uid!!,true)
                    FirebaseDatabase.getInstance().getReference("groupChatrooms").child("$gid/users").setValue(groupChatList[position].users)
                }
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            Log.d("getItemCount","${groupChatList.size}")
            return groupChatList.size
        }
    }
}