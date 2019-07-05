package com.example.lovebirds.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.lovebirds.Activities.UserInfoActivity
import com.example.lovebirds.R
import com.example.lovebirds.util.User

class CardAdapter(context: Context?,resourceId:Int,users:List<User>):ArrayAdapter<User>(context,resourceId,users)
{

    // THIS ADAPTER CREATES OUR CARDS
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var user = getItem(position)
        var finalView = convertView?:LayoutInflater.from(context).inflate(R.layout.item,parent,false)

        var name =finalView.findViewById<TextView>(R.id.nameTV)     // storing these values in an variable we use this because they are present in the item.xml
        var image =finalView.findViewById<ImageView>(R.id.photoIV_card)
        var userInfo =finalView.findViewById<LinearLayout>(R.id.userInfoLayout) // object of Linear Layout

        name.text="${user.name},${user.age}"
        Glide.with(context).load(user.imageUrl).into(image)

        userInfo.setOnClickListener {
            finalView.context.startActivity(UserInfoActivity.newIntent(finalView.context,user.uid)) // this line will start userinfon activity whenever the user clicks on the linear layout
            //new intent function will be called in the process of starting the new activity
        }



        return finalView
    }
}