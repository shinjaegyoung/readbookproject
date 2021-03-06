package com.example.readbook.fragment

import android.animation.ObjectAnimator
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
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.readbook.*
import com.example.readbook.calendar.CalendarMainActivity
import com.example.readbook.databinding.FragmentProfileBinding
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
private var isFabOpen = false
class ProfileFragment : Fragment() {
   // private var testDD : ArrayList<User> = ArrayList()
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



    //???????????? ???????????? ???
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    //?????????????????? ???????????? ?????? ??????????????? ????????? ???
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    /*private fun toggleFab(){
        Log.d("toggle", "toggleFab2")
        if(isFabOpen){
            ObjectAnimator.ofFloat(floatingActioncall, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(floatingsevicecenterbtn, "translationY", 0f).apply { start() }
            ObjectAnimator.ofFloat(floatingmain, View.ROTATION, 45f, 0f).apply { start() }
            floatingmain.setImageResource(R.drawable.callp)

            }else{
                ObjectAnimator.ofFloat(floatingActioncall, "translationY", -200f).apply { start() }
            ObjectAnimator.ofFloat(floatingsevicecenterbtn, "translationY", -400f).apply { start() }
            ObjectAnimator.ofFloat(floatingmain, View.ROTATION, 0f, 45f).apply { start() }
            floatingmain.setImageResource(R.drawable.callp)
        }
        isFabOpen = !isFabOpen
    }*/

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                imageUri = result.data?.data //????????? ?????? ??????
                profile_imageview.setImageURI(imageUri) //????????? ?????? ??????

                //?????? ????????? ?????? ??? ????????? ????????? ??????
                fireStorage.child("userImages/$uid/photo").delete().addOnSuccessListener {
                    fireStorage.child("userImages/$uid/photo").putFile(imageUri!!).addOnSuccessListener {
                        fireStorage.child("userImages/$uid/photo").downloadUrl.addOnSuccessListener {
                            val photoUri : Uri = it
                            println("$photoUri")
                            fireDatabase.child("users/$uid/profileImageUrl").setValue(photoUri.toString())
                            Toast.makeText(requireContext(), "?????????????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                Log.d("?????????", "??????")
            }
            else{
                Log.d("?????????", "??????")
            }
        }
    //?????? ??????????????? ???
    //?????????????????? ??????????????? ?????????????????? ??????
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        /*val binding = FragmentProfileBinding.inflate(inflater, container, false)*/

        //vie ????????? ????????? return??? ?????? ??????????????? glide??? ????????? ??????
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
        //????????? ??????
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

        view.my_productBtn.setOnClickListener {
            val intent = Intent(context, MyProductActivity::class.java)
            context?.startActivity(intent)
        }

        view.signoutBtn.setOnClickListener {
            //firebase auth?????? sign out ?????? ??????
            Log.d("test1" , "${auth.currentUser.toString()}")
            if(auth.currentUser != null){
                Firebase.auth.signOut()
            }
            var intent=Intent(context, SplashActivity::class.java) //????????? ????????? ??????
            startActivity(intent!!)
            activity?.finish()
        }
        //??????????????? ?????????
        photo?.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }
        button?.setOnClickListener{
            if(name?.text!!.isNotEmpty()) {
                fireDatabase.child("users/$uid/name").setValue(name.text.toString())
                name.clearFocus()
                Toast.makeText(requireContext(), "????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
            }
        }





        floatingActionButton?.setOnClickListener{


            var servicecenter = "XuBJ8JfS9bRd1JEBVa83rd5CLkT2"



            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("destinationUid", servicecenter)
            context?.startActivity(intent)
            Log.d("useruid", "${user?.uid.toString()}")
            Toast.makeText(requireContext(),"??????????????? readbook ???????????? ????????? ????????? ????????? ?????????????????? ???????????????????????????", Toast.LENGTH_SHORT).show()

        }

        darkMode?.setOnCheckedChangeListener{ _, onSwitch ->
            if(onSwitch) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkMode.isChecked = true


                //Dark ?????? ??????

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //Light  ?????? ??????
            }
           /* val intent = Intent(context,HomeFragment::class.java)
                context?.startActivity(intent)
                activity?.finish()*/
        }

        // ?????????, ??????????????? ??????
        calBtn?.setOnClickListener {
            val intent = Intent(context, CalendarMainActivity::class.java)
            context?.startActivity(intent)
        }

        diaryBtn?.setOnClickListener {
            val intent = Intent(context, ReadListActivity::class.java)
            context?.startActivity(intent)
        }



        // --------------------????????????
//        if (radioMode != null) {
//            radioMode.setOnCheckedChangeListener { _, checkedId ->
//                when (checkedId) {
//                    R.id.rbLight -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        //Light  ?????? ??????
//                    }
//                    R.id.rbDark -> {
//                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                        //Dark ?????? ??????
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