package com.selfworking.messageapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.selfworking.messageapp.Messages.LatestMessgesActivity
import com.selfworking.messageapp.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)



        login_button_login.setOnClickListener{
            //Toast.makeText(this , "Clicked" , Toast.LENGTH_LONG).show()
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            if(email.isEmpty()  || password.isEmpty()){
                Toast.makeText(this , "Please Enter Email and Password" , Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
                auth = Firebase.auth

                auth.signInWithEmailAndPassword(email , password)
                    .addOnCompleteListener {
                        if(!it.isSuccessful){
                            Toast.makeText(baseContext, "Login failed.",
                                Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        else{
                            Toast.makeText(baseContext, "Login Successful.",
                                Toast.LENGTH_SHORT).show()
                            // Log.d("Main", "Loged IN " + it.result.user.uid)
                            val intent =  Intent(this , LatestMessgesActivity::class.java )
                            intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }
                }

                    .addOnFailureListener{
                        Toast.makeText(this, "Login Failed. + ${it.message}",
                            Toast.LENGTH_SHORT).show()
                        return@addOnFailureListener
                    }
            }


        }
    }

