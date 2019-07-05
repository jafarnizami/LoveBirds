package com.example.lovebirds.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lovebirds.R
import com.example.lovebirds.util.DATA_USERS
import com.example.lovebirds.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val firebaseDatabase =FirebaseDatabase.getInstance().reference
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        //when user is already created and authenticated
        val user =firebaseAuth.currentUser
        if (user!=null)
        {
            startActivity(Intent(MainActivity.newIntent(this)))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
    }

    override fun onStart() {
        //since we start the our compilation from the signup button so we gotta check whether the user has already signed in or not!!!
        // so this function will get called when the activiy starts.... and after this fuction is called we will go back the signp fun if there is a new user
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    fun onSignup(v:View)
    {
        //when user will click on signup button this function will be called and ive added this clicklistenr in the xml file
        val email = emailET.text.toString()
        val pass =passwordET.text.toString()
        if (!email.isNullOrEmpty() && !pass.isNullOrEmpty() )
        {
            firebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        val Email = emailET.text.toString()
                        val userId =firebaseAuth.currentUser?.uid ?:""

                        val user =User(userId,"","",Email,"","","","")
                        firebaseDatabase.child(DATA_USERS).child(userId).setValue(user)


                        Toast.makeText(this@SignUpActivity,"Registration Successfull",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this@SignUpActivity,"Failed due to:${it.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
                }
        }
        else
        {
            Toast.makeText(this@SignUpActivity,"Empty Field",Toast.LENGTH_SHORT).show()


        }
    }
    companion object {
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }

}
