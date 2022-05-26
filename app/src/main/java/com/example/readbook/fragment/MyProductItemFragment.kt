package com.example.readbook.fragment

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
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbook.ProductDetailActivity
import com.example.readbook.R
import com.example.readbook.database
import com.example.readbook.databinding.FragmentMyProductItemBinding
import com.example.readbook.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class MyProductItemFragment : Fragment() {
    companion object {
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private var recyclerView: RecyclerView? = null
        private lateinit var binding: FragmentMyProductItemBinding

        fun newInstance(): MyProductItemFragment {
            return MyProductItemFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
        recyclerView = binding.myProductRecyclerview
        val manager = LinearLayoutManager(this.context)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        recyclerView?.layoutManager=manager
    }
    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyProductItemBinding.inflate(layoutInflater, container, false)
        binding.myProductRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.myProductRecyclerview.adapter = RecyclerViewAdapter()

        return binding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {
        // 작성한 product db를 담는 ArrayList 생성
        private val myProductlist = ArrayList<Product>()

        init {

            fireDatabase.child("productlist")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("상품리스트","for문 이전................................")
                        myProductlist.clear()
                        for (data in snapshot.children) {
                            Log.d("상품리스트","for문 내부................................")
                            Log.d("상품리스트","${data.value}")
                            var puid = data.getValue<Product>()?.user.toString()
                            var uid = Firebase.auth.currentUser?.uid.toString()
                            if(puid == uid) {
                                myProductlist.add(data.getValue<Product>()!!)
                                Log.d("상품리스트: productlist", "${myProductlist}")
                                println(data)
                            }
                        }
                        notifyDataSetChanged()
                    }
                })
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_my_product, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.productImg)
            val textView_title: TextView = itemView.findViewById(R.id.tvProductName)
            val textView_price: TextView = itemView.findViewById(R.id.tvPrice)
            val soldoutBtn: Button = itemView.findViewById(R.id.soldoutBtn)

        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            fireDatabase.child("productlist").child("${myProductlist[position].pid!!}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        // 첫 번째로 등록한 이미지를 썸네일로 가져오기(Glide)
                        FirebaseStorage.getInstance().reference.child("productImages")
                            .child("${myProductlist[position].pid!!}/0").downloadUrl
                            .addOnCompleteListener{ task ->
                                if(task.isSuccessful){
                                    Glide.with(holder.itemView.context)
                                        .load(task.result)
                                        .override(200,200)
                                        .centerCrop()
                                        .into(holder.imageView)
                                }
                            }
                        // 상품이름, 가격 item 텍스트뷰에 저장
                        holder.textView_title.text = myProductlist[position].pName.toString()
                        holder.textView_price.text = myProductlist[position].pPrice.toString()
                    }
                })

            //상품 선택 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                intent.putExtra("pDes", myProductlist[position].pDes)
                intent.putExtra("pName", myProductlist[position].pName)
                intent.putExtra("pPrice", myProductlist[position].pPrice)
                intent.putExtra("pViewCount", myProductlist[position].pViewCount)
                intent.putExtra("pid", myProductlist[position].pid)
                intent.putExtra("status", myProductlist[position].status)
                intent.putExtra("user", myProductlist[position].user)
                intent.putExtra("regdate", myProductlist[position].regDate.toString())
                Log.d("lyk","${myProductlist[position]}")
                context?.startActivity(intent)
            }

            //판매완료 버튼 클릭시
            holder.soldoutBtn.setOnClickListener {
                fun updateDatas(status: String) {
                    database = FirebaseDatabase.getInstance().getReference("productlist").child("${myProductlist[position].pid!!}")
                    val product = mapOf<String, Any?>(
                        "status" to status
                    )
                    Log.d("update 실행","실행됨 ...... ${myProductlist[position].pid!!} / ${myProductlist[position].status}")
                    database.updateChildren(product)
                }
                updateDatas("판매 완료")
                holder.soldoutBtn.text = "판매완료"
                holder.soldoutBtn.isEnabled=false
                Toast.makeText(context,"상품이 판매 완료되었습니다.",Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int {
            Log.d("상품리스트", "${myProductlist.size}")
            return myProductlist.size
        }
    }

}