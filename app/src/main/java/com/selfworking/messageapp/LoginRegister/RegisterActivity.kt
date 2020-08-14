package com.selfworking.messageapp.LoginRegister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.selfworking.messageapp.Messages.LatestMessgesActivity
import com.selfworking.messageapp.Model.user
import com.selfworking.messageapp.R
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.login_button_login
import kotlinx.android.synthetic.main.activity_register.password_edittext_login
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        login_button_login.setOnClickListener{
            performRegister()
        }

        already_have_account_register.setOnClickListener{

            val intent =  Intent(this , LoginActivity::class.java)
            startActivity(intent)
            Log.d("RegisterActivity" ,"Show Login Screen")

        }

        select_image_register.setOnClickListener{
            val intent  = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    //This function is handeling User Registration
    private fun performRegister(){
        val email = email_edittext_registration.text.toString()
        val password = password_edittext_login.text.toString()

        if(email.isEmpty()  || password.isEmpty()){
            Toast.makeText(this , "Please Enter Email and Password" , Toast.LENGTH_LONG).show()
            return
        }
        Log.d("RegisterActivity" ,"Email is : $email")

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email , password)
            .addOnCompleteListener{

                if(!it.isSuccessful){
                    Toast.makeText(baseContext, "Registration failed.",
                        Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }

                else{
                    Toast.makeText(baseContext, "Registration Successful.",
                        Toast.LENGTH_SHORT).show()

                    Log.d("RegisterActivity" ,"Created a new user")

                    uploadImageToFirebase()
                    // Log.d("Main", "Loged IN " + it.result.user.uid)
                }
            }

            .addOnFailureListener{
                Toast.makeText(this, "Registration Failed. + ${it.message}",
                    Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }


    }



    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==  0 && resultCode == Activity.RESULT_OK  && data != null){
            selectedPhotoUri =  data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver ,  selectedPhotoUri)

            profile_image_register.setImageBitmap(bitmap)
            select_image_register.alpha = 0f
//            val bitmapDrawable =  BitmapDrawable(bitmap)
//            select_image_register.setBackgroundDrawable(bitmapDrawable)
//            select_image_register.text = ""

            Log.d("RegisterActivity" ,"Image selected and Shown")
        }
    }


    private fun uploadImageToFirebase(){

        if (selectedPhotoUri == null)
            return

        val filename = UUID.randomUUID().toString()

        val ref  =FirebaseStorage.getInstance().getReference("/images/$filename" )


        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("RegisterActivity" ,"Image Uploaded To Firebase: ${it.metadata?.path}")
             ref.downloadUrl.addOnSuccessListener {
                 Log.d("RegisterActivity" ,"File Location: $it")

                 saveUserToFirebaseDatabase(it.toString())
             }

        }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){

        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = user(uid.toString() , username_edit_text_registration.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity" , "User Saved to Database")

                val intent =  Intent(this , LatestMessgesActivity::class.java )
                intent.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("RegisterActivity" , "Error Adding User to Database ${it.message}")
            }
    }
}


