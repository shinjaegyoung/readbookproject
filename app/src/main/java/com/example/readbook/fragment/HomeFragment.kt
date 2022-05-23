package com.example.readbook.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dinuscxj.progressbar.CircleProgressBar
import com.example.readbook.ProductRegActivity
import com.example.readbook.R
import com.example.readbook.calendar.CalendarMainActivity
import com.example.readbook.model.User
import com.example.readbook.databinding.FragmentHomeBinding
import com.example.readbook.model.CalendarData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    companion object{
        lateinit var binding : FragmentHomeBinding
        lateinit var homeRecyclerView : RecyclerView
        lateinit var homeAdapter : HomeRecyclerViewAdapter

        private var imageUri : Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private var banner : ArrayList<String>? = null

        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()

        private val curYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis())).toString()
        private val curMonth = SimpleDateFormat("M").format(Date(System.currentTimeMillis())).toString()

        private var circleProgressBar: CircleProgressBar? = null
        private val DEFAULT_PATTERN = "%d%%"
        private var check = 0

        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    init{
        banner = ArrayList<String>()
        banner!!.add("https://sejong.nl.go.kr/comm/getImage.do?atchFileId=97850&fileSn=3946") //https://youtu.be/pVtgDojeW2U
        banner!!.add("https://sejong.nl.go.kr/comm/getImage.do?atchFileId=97918&fileSn=3987") //https://youtu.be/QHFggfeTB9Q
        banner!!.add("https://sejong.nl.go.kr/comm/getImage.do?atchFileId=97913&fileSn=3980") //https://youtu.be/GQUPgnccNEU
        banner!!.add("https://sejong.nl.go.kr/comm/getImage.do?atchFileId=97863&fileSn=3958") //https://youtu.be/IJNB2Y9oSjQ
        banner!!.add("https://sejong.nl.go.kr/comm/getImage.do?atchFileId=97881&fileSn=3959") //https://youtu.be/3a7cPFm-WoQ
    }

    fun getDaysInMonth(month:Int, year:Int):Int {
        return when(month-1){
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

    fun format(progress: Int, max: Int): CharSequence {
        return String.format(DEFAULT_PATTERN, (progress.toFloat() / max.toFloat() * 100).toInt())
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        var dateMax = getDaysInMonth(curMonth.toInt(), curYear.toInt())

        Log.d("DB","${fireDatabase.child("calendar").child("$uid").child("$curYear")
            .child("$curMonth")}")
        fireDatabase.child("calendar").child("$uid").child("$curYear")
            .child("$curMonth").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("dayCount", "fail...................")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    check=0
                    for (data in snapshot.children) {
                        if(data.getValue<CalendarData>()?.count == "1"){
                            check++
                        }//else if(data.getValue<CalendarData>()?.count == null){
                            //fireDatabase.child("calendar").child("$uid").child("$curYear")
                          //      .child("$curMonth").setValue("${data.children}")
                       // }
                    }
                    binding.homeCpbContent1.text = "$check"
                    Log.d("출석체크 count","$check")
                    Log.d("출석체크 dateMax","$dateMax")

                    Log.d("dd","${format(check,dateMax)}")
                    circleProgressBar = binding.cpbCirclebar
                    circleProgressBar?.progress = (check.toFloat() / dateMax.toFloat() * 100).toInt()
                }
            })

        binding.homeCpbTitle.text="${curYear}년 ${curMonth}월의 독서활동은"
        binding.homeCpbContent2.text="회 입니다"

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.homeRecyclerview.layoutManager=layoutManager
        binding.homeRecyclerview.adapter= HomeRecyclerViewAdapter()

        binding.challengeView.setOnClickListener {
            val intent= Intent(context, CalendarMainActivity::class.java)
            context?.startActivity(intent)
        }

        return binding.root
    }

    inner class HomeRecyclerViewAdapter : RecyclerView.Adapter<HomeFragment.HomeRecyclerViewAdapter.ViewHolder>() {

        inner class ViewHolder(view:View?) : RecyclerView.ViewHolder(view!!){
            val bannerImg = view?.findViewById<ImageView>(R.id.item_home_banner)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_home,parent,false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: HomeFragment.HomeRecyclerViewAdapter.ViewHolder,
            position: Int
        ) {
            Glide.with(holder.itemView.context)
                .load(banner!![position])
                .centerCrop()
                .into(holder.bannerImg!!)
            holder.bannerImg.clipToOutline = true
        }

        override fun getItemCount(): Int {
            return banner!!.size
        }
    }
}