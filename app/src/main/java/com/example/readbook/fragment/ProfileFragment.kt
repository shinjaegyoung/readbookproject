package com.example.readbook.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.*
import com.example.readbook.calendar.CalendarMainActivity
import com.example.readbook.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var intentLauncher: ActivityResultLauncher<Intent>

    companion object{
        private var imageUri : Uri? = null
        private val fireStorage = FirebaseStorage.getInstance().reference
        private val fireDatabase = FirebaseDatabase.getInstance().reference
        private val user = Firebase.auth.currentUser
        private val uid = user?.uid.toString()
        private var auth = Firebase.auth

        fun newInstance() : ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }



    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                profile_imageview.setImageURI(imageUri) //이미지 뷰를 바꿈

                //기존 사진을 삭제 후 새로운 사진을 등록
                fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                    fireStorage.child("userImages/$uid/photo").putFile(imageUri!!).addOnSuccessListener {
                        fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener {
                            val photoUri : Uri = it
                            println("$photoUri")
                            fireDatabase.child("users/$uid/profileImageUrl").setValue(photoUri.toString())
                            Toast.makeText(requireContext(), "프로필사진이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Log.d("이미지", "성공")
            }
            else{
                Log.d("이미지", "실패")
            }
        }
    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        //vie 선언을 안하고 return에 바로 적용시키면 glide가 작동을 안함
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val photo = view?.findViewById<ImageView>(R.id.profile_imageview)

        val email = view?.findViewById<TextView>(R.id.profile_textview_email)
        val name = view?.findViewById<TextView>(R.id.profile_textview_name)
        val button = view?.findViewById<TextView>(R.id.profile_button)
        //val radioMode = view?.findViewById<RadioGroup>(R.id.radioMode)
        val darkMode = view?.findViewById<SwitchCompat>(R.id.switch_darkmode)
        val calBtn = view?.findViewById<Button>(R.id.toCalBtn)
        val diaryBtn = view?.findViewById<Button>(R.id.toDiaryBtn)

        val floatingActionButton = view?.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        //프로필 구현
        fireDatabase.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue<User>()
                println(userProfile)
                Glide.with(requireContext()).load(userProfile?.profileImageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(photo!!)
                email?.text = userProfile?.email
                name?.text = userProfile?.name
            }
        })

        view.signoutBtn.setOnClickListener {
            //firebase auth에서 sign out 기능 호출
            Log.d("test1" , "${auth.currentUser.toString()}")
            if(auth.currentUser != null){
                Firebase.auth.signOut()
            }
            var intent=Intent(context, SplashActivity::class.java) //로그인 페이지 이동
            startActivity(intent!!)
            activity?.finish()
        }
        //프로필사진 바꾸기
        photo?.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        button?.setOnClickListener{
            if(name?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        floatingActionButton?.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01012345678"))
            startActivity(intent)
        }

        darkMode?.setOnCheckedChangeListener{ _, onSwitch ->
            if(onSwitch) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkMode.isChecked = true


                //Dark 모드 설정

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //Light  모드 설정
            }
           /* val intent = Intent(context,HomeFragment::class.java)
                context?.startActivity(intent)
                activity?.finish()*/
        }

        // 캘린더, 다이어리로 이동
        calBtn?.setOnClickListener {
            val intent = Intent(context, CalendarMainActivity::class.java)
            context?.startActivity(intent)
        }

        diaryBtn?.setOnClickListener {
            val intent = Intent(context, ReadListActivity::class.java)
            context?.startActivity(intent)
        }



        // --------------------다크모드
//        if (radioMode != null) {
//            radioMode.setOnCheckedChangeListener { _, checkedId ->
//                when (checkedId) {
//                    R.id.rbLight -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        //Light  모드 설정
//                    }
//                    R.id.rbDark -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                        //Dark 모드 설정
//
//                    }
//                    R.id.rbDefault -> {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                        } else {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
//                        }
//                    }
//                }
//            }
//        }
        return view
    }



}