package com.example.lovebirds.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.lovebirds.R
import com.example.lovebirds.util.DATA_USERS
import com.example.lovebirds.util.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_info.*

class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val userId = intent.extras.getString(PARAM_USER_ID,"") //pulling the userId value fetched by the newIntent function......
        if (userId.isNullOrEmpty())
        {
            finish()
        }
         val userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
        userDatabase.child(userId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) { //pulling the values of the selectd user from the database
                val user = p0.getValue(User::class.java)
                userInfoName.text=user?.name
                userInfoAge.text=user?.age
                userInfoAbout.text=user?.about
                if (userInfoIV!=null)
                {
                    Glide.with(this@UserInfoActivity).load(user?.imageUrl).into(userInfoIV)
                }
            }
        })

    }
    companion object{

        private val PARAM_USER_ID ="User id"
         fun newIntent(context: Context,userId :String?):Intent  // this function is basically used to get the value of the userIdof the selected user so that it can be used to fetch all the details in the User Info Activity
         {
             val intent=Intent(context,UserInfoActivity::class.java)
             intent.putExtra(PARAM_USER_ID,userId) // we will put the values of the userId in the PARAM_USER_ID
             return intent
         }
    }
}
