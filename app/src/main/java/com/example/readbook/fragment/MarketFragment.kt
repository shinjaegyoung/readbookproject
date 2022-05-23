package com.example.readbook.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.readbook.ProductDetailActivity
import com.example.readbook.ProductRegActivity
import com.example.readbook.R
import com.example.readbook.databinding.FragmentMarketBinding
import com.example.readbook.model.Product
import com.example.readbook.model.ProductImg
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList

private val fireDatabase = FirebaseDatabase.getInstance().reference

class MarketFragment : Fragment() {
    lateinit var binding: FragmentMarketBinding
    companion object {
        fun newInstance(): MarketFragment {
            return MarketFragment()
        }
    }
    // 메모리에 올라감
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMarketBinding.inflate(layoutInflater, container, false)
        binding.marketfragmentRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.marketfragmentRecyclerview.adapter = RecyclerViewAdapter()

        // 작성 버튼 클릭 시 페이지 이동
        binding.btnRegMarket.setOnClickListener {
            val intent=Intent(context, ProductRegActivity::class.java)
            context?.startActivity(intent)
        }
        return binding.root
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {
        // 작성한 product db를 담는 ArrayList 생성
        private val productlist = ArrayList<Product>()

        init {
            fireDatabase.child("productlist")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("상품리스트","for문 이전................................")
                        productlist.clear()
                        for (data in snapshot.children) {
                            Log.d("상품리스트","for문 내부................................")
                            Log.d("상품리스트","${data.value}")
                            productlist.add(data.getValue<Product>()!!)
                            Log.d("상품리스트: productlist","${productlist}")
                            println(data)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_market, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.productImg)
            val textView_title: TextView = itemView.findViewById(R.id.tvProductName)
            val textView_price: TextView = itemView.findViewById(R.id.tvPrice)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            fireDatabase.child("productlist").child("${productlist[position].pid!!}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        // 첫 번째로 등록한 이미지를 썸네일로 가져오기(Glide)
                        FirebaseStorage.getInstance().reference.child("productImages")
                            .child("${productlist[position].pid!!}/0").downloadUrl
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
                        holder.textView_title.text = productlist[position].pName.toString()
                        holder.textView_price.text = productlist[position].pPrice.toString()
                    }
                })

            //상품 선택 시 이동(페이지 구현 예정)
            holder.itemView.setOnClickListener {
                val intent = Intent(activity, ProductDetailActivity::class.java)
                intent.putExtra("pDes", productlist[position].pDes)
                intent.putExtra("pName", productlist[position].pName)
                intent.putExtra("pPrice", productlist[position].pPrice)
                intent.putExtra("pViewCount", productlist[position].pViewCount)
                intent.putExtra("pid", productlist[position].pid)
                intent.putExtra("status", productlist[position].status)
                intent.putExtra("user", productlist[position].user)
                intent.putExtra("regdate", productlist[position].regDate.toString())
                Log.d("lyk","${productlist[position]}")
                context?.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            Log.d("상품리스트", "${productlist.size}")
            return productlist.size
        }
    }
}