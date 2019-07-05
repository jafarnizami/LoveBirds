package com.example.lovebirds.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.lovebirds.R

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }
    fun onLogin(v:View)
    {
        startActivity(LoginActivity.newIntent(this))
    }
    fun onSignup(v:View)
    {
        startActivity(SignUpActivity.newIntent(this))

    }
    companion object {
        fun newIntent(context: Context) = Intent(context, StartActivity::class.java)
    }
}
