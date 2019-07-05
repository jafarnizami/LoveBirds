package com.example.lovebirds.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lovebirds.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.passwordET

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth =FirebaseAuth.getInstance()
    private val firebaseAuthListener =FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if(user!=null)
        {
            startActivity(Intent(MainActivity.newIntent(this)))
            finish()

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
        // i put finish() here by mistake and coudnt find the error for hours Lol hahahahh......
    }
    fun onLogin(v:View) {
        val email = emailETlog.text.toString()
        val pass = passwordETlog.text.toString()
        if (!email.isNullOrEmpty() && !pass.isNullOrEmpty())
        {
            firebaseAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@LoginActivity,"Login Successfull",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this@LoginActivity,"Login failed due to: ${it.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()

                    }
                }
        }
        else
        {
            Toast.makeText(this@LoginActivity,"Empty Field",Toast.LENGTH_SHORT).show()

        }
    }
    companion object {
        // companion object can access methods that are private inside the class // same as making fuctions static  in java
        //whem i want to use a function outside of my class instance(boundary)
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}