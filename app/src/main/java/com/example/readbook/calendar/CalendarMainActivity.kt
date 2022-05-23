package com.example.readbook.calendar

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readbook.MainActivity
import com.example.readbook.R
import com.example.readbook.model.CalendarData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

private val curYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis())).toString()
private val curMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis())).toString()
private val curDate = SimpleDateFormat("dd").format(Date(System.currentTimeMillis())).toString()
private var auth = Firebase.auth
val user = auth.currentUser

class CalendarMainActivity : AppCompatActivity() {
    private lateinit var texMonth: TextView
    private var calendarAdapter:CalendarAdapter= CalendarAdapter(this@CalendarMainActivity)
    private val calendar = Calendar.getInstance()
    private var month=calendar.get(Calendar.MONTH)
    private var year=calendar.get(Calendar.YEAR)

    private var testDC : ArrayList<CalendarData?> = ArrayList<CalendarData?>()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun checkStamp() {

        FirebaseDatabase.getInstance().reference.child("calendar")
            .child("${user?.uid}").child("$year")
            .child("${(month)+1}").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.d("dayCount", "fail...................")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Log.d("dayCount", "${snapshot.children}")
                    //Log.d("내부testDC", "success..................")
                    //testDC.clear()
                    Log.d("내부testDC", "$testDC")
                    for (data in snapshot.children) {
                        testDC.add(data.getValue<CalendarData>())
                        //println(data)
                        if(data.getValue<CalendarData>()?.count == null){
                            FirebaseDatabase.getInstance().reference.child("calendar")
                                .child("${user?.uid}").child("$year")
                                .child("${(month)+1}").setValue("${data.children}")
                        }
                    }
                    //notifyDataSetChanged()
                    Log.d("내부testDC", "$testDC")
                    val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
                    recyclerView.layoutManager= GridLayoutManager(this@CalendarMainActivity, 7)
                    //GridLayoutManager 달력이기 때문에 바둑판형식으로 해줌
                    recyclerView.adapter = calendarAdapter
                }
            })
    }

    init{
        checkStamp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_main)

        setSupportActionBar(findViewById(R.id.topBar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        texMonth = findViewById(R.id.text_month)

        val btnLeft : ImageButton = findViewById<ImageButton>(R.id.btn_left)
        val btnRight : ImageButton = findViewById<ImageButton>(R.id.btn_right)
        btnLeft.setOnClickListener {
            testDC.clear()
            var month = calendar.get(Calendar.MONTH) - 1
            var year = calendar.get(Calendar.YEAR)
            if(month == -1){
                month == 11
                year = year - 1
            }
            calendar.set(year, month, calendar.get(Calendar.DAY_OF_MONTH))
            calendarShow(calendar)
        }
        btnRight.setOnClickListener {
            testDC.clear()
            var month = calendar.get(Calendar.MONTH) + 1
            var year = calendar.get(Calendar.YEAR)
            if(month == 12){
                month = 0
                year = year + 1
            }
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            calendarShow(calendar)
        }

        checkStamp()
        calendarShow(calendar)

    }

    override fun onStart() {
        super.onStart()

    }

    private fun calendarShow(calendar: Calendar){
        val newCalendar = Calendar.getInstance()
        newCalendar.timeInMillis = calendar.timeInMillis
        texMonth.setText(SimpleDateFormat("yyyy.MM").format(newCalendar.time))
        val firstDay = newCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)
        val lastDay = newCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val arrayDay = ArrayList<Long>()

        for (i: Int in firstDay..lastDay){
            newCalendar.set(Calendar.DAY_OF_MONTH,i)
            val dayOfWeek = newCalendar.get(Calendar.DAY_OF_WEEK)
            if (i == 1 && dayOfWeek > 1) {
                for(j: Int in 1..dayOfWeek -1) {
                    val lastCalendar = Calendar.getInstance()
                    month = newCalendar.get(Calendar.MONTH) - 1
                    year = newCalendar.get(Calendar.YEAR)
                    if(month == -1){
                        month = 11
                        year =year - 1
                    }
                    lastCalendar.set(Calendar.YEAR, year)
                    lastCalendar.set(Calendar.MONTH, month)
                    val lastMonth_lastDay =
                        (lastCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - (j - 1))
                    lastCalendar.set(Calendar.DAY_OF_MONTH, lastMonth_lastDay)
                    arrayDay.add(lastCalendar.timeInMillis)
                    Collections.sort(arrayDay)
                }
            }
            arrayDay.add(newCalendar.timeInMillis)

        }
        calendarAdapter.setList(arrayDay, newCalendar.get(Calendar.MONTH))

    }

    inner class  CalendarAdapter(val context: Context): RecyclerView.Adapter<CalendarAdapter.ItemView>(){
        var month = 0
        var calendardata : CalendarData = CalendarData("0")
        private val calendar = Calendar.getInstance()
        private val array = ArrayList<Long>()

        fun getDaysInMonth(month:Int, year:Int):Int {
            return when(month-1){
                Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
                Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
                Calendar.FEBRUARY -> if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
                else -> throw IllegalArgumentException("Invalid Month")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemView {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
            return ItemView(view)
        }

        override fun onBindViewHolder(holder: ItemView, position: Int) {
            calendardata= CalendarData("0")

            val calendar = Calendar.getInstance()
            var dayCounts = getDaysInMonth(curMonth.toInt(), curYear.toInt())-1
            calendar.timeInMillis = array.get(position)
            val month = calendar.get(Calendar.MONTH)
            if (this.month != month){
                holder.background.setBackgroundColor(Color.LTGRAY)
            } else {
                holder.background.setBackgroundColor(Color.WHITE)
            }

            holder.textDay.text = SimpleDateFormat("dd").format(calendar.time)


            var addCount = FirebaseDatabase.getInstance().getReference("calendar")
                .child("${user?.uid}")
                .child("$year").child("${(month)+1}")
                .child("${SimpleDateFormat("dd").format(calendar.time)}")

            FirebaseDatabase.getInstance().reference.child("calendar")
                .child("${user?.uid}")
                .child("$year").child("${(month)+1}")
                .child("${SimpleDateFormat("dd").format(calendar.time)}")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value ==null){
                            addCount.setValue(calendardata)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

            //Log.d("dayCounts", "$dayCounts")
            Log.d("testDC", "$testDC")
            for(data in testDC) {
                if (testDC[position]?.count!! == "1") {
                    Log.d("forfor", "success...................")
                    holder.stamp.visibility = View.VISIBLE
                } else {
                    holder.stamp.visibility = View.INVISIBLE
                }
            }


            holder.itemView.setOnClickListener{
                Log.d("pgm","${SimpleDateFormat("dd").format(calendar.time)}")
                // Log.d("kikiki", "${dayCounts}")


                if(curDate == holder.textDay.text){
                    if (!holder.stamp.isVisible){
                        testDC.clear()
                        // 1을 저장하는 코드를 넣어주고
                        holder.stamp.visibility = View.VISIBLE
                        Toast.makeText(
                            context,
                            SimpleDateFormat("yyyy-MM-dd 출석 완료!\n오늘도 즐거운 독서 생활 하세요!").format(calendar.time),
                            Toast.LENGTH_SHORT
                        ).show() // 클릭을 햇을시 해당 년월일 표시해줌.show()

                        calendardata= CalendarData("1")
                        addCount.setValue(calendardata)

                    }else if(holder.stamp.isVisible){
                        testDC.clear()
                        // 0을 저장하는 코드를 넣어주고
                        holder.stamp.visibility = View.INVISIBLE

                        calendardata= CalendarData("0")
                        addCount.setValue(calendardata)
                    }
                }else{
                    Toast.makeText(
                        context,
                        SimpleDateFormat("오늘 날짜에 출석 체크해주세요").format(calendar.time),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                /*val intent = Intent(context, CalendarDetailActivity::class.java)
                context.startActivity(intent)*/
            }
        }

        override fun getItemCount(): Int {
            return array.size // 아이템 갯수
        }

        fun setList(array: ArrayList<Long>, month:Int){
            this.month = month
            this.array.clear()
            this.array.addAll(array)
            notifyDataSetChanged()
        }

        inner class ItemView(view: View) : RecyclerView.ViewHolder(view){
            val stamp : ImageView = view.findViewById(R.id.imageview)
            val textDay: TextView = view.findViewById(R.id.text_day)
            val background : ConstraintLayout =view.findViewById(R.id.background)
        }

    }
}