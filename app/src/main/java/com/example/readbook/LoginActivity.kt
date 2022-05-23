package com.example.readbook


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

private lateinit var auth : FirebaseAuth
private lateinit var firebaseAuth : FirebaseAuth
class LoginActivity : AppCompatActivity() {
    var callbackManager : CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        firebaseAuth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        val email = findViewById<EditText>(R.id.et_login_id)
        val password = findViewById<EditText>(R.id.et_login_password)
        val btn_login = findViewById<Button>(R.id.profile_button)

        btn_login.setOnClickListener {
            if(email.text.isEmpty() && password.text.isEmpty()){
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                Log.d("Email", "$email, $password")
                email.setText("")
                password.setText("")
            }
            else{
                signIn(email.text.toString(), password.text.toString())
            }
        }

        //회원가입창 Intext
        val btn_registration = findViewById<Button>(R.id.btn_registration)
        btn_registration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    private fun signIn(email: String, password : String){
        // [start sign_in_with_email
        //val intentMain = Intent(this, MainActivity::class.java)
        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Log.d("로그인", "성공")
                    val user = auth.currentUser
                    updateUI(user)
                    finish()
                    startActivity(intentMain)
                }else{
                    Toast.makeText(this, "정확한 아이디와 비밀번호 입력해주세요", Toast.LENGTH_SHORT).show()
                    Log.d("로그인", "실패")
                    updateUI(null)
                }
            }
        // [end sign_in_with_email
    }

    private fun updateUI(user: FirebaseUser?){}

    public override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {}

}


