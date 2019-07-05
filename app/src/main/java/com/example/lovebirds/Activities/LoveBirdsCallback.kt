package com.example.lovebirds.Activities

import com.google.firebase.database.DatabaseReference

// we make this inteface so that we can use all of these function without creating them again and again
interface LoveBirdsCallback {

    fun onSignout()
    fun onGetUserId():String
    fun getUserDatabase():DatabaseReference
    fun getChatDatabase():DatabaseReference
    fun profileComplete()
    fun startActivityForPhoto()
}