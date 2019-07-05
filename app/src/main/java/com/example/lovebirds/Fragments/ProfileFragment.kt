package com.example.lovebirds.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.lovebirds.Activities.LoveBirdsCallback

import com.example.lovebirds.R
import com.example.lovebirds.util.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {

    private lateinit var userId:String
    private lateinit var userDatabase: DatabaseReference
    private var callback:LoveBirdsCallback?=null


    fun setCallback(callback: LoveBirdsCallback)
    {
        // setcallback is used to give the reference of the LoveBirds callback in this fragment
        this.callback=callback
        userId = callback.onGetUserId()
        userDatabase=callback.getUserDatabase().child(userId) // this will pint to the user stored in our firebase database

    }
    //by mistake i made this function outside the class scope and was unable to find the error for hours!! lOl......


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progresslayout.setOnTouchListener {view, event ->true }

        populateinfo()

        photoIV.setOnClickListener { callback?.startActivityForPhoto() }

        applybutton.setOnClickListener {
            onApply()
        }
        signoutbutton.setOnClickListener {
            callback?.onSignout()
        }

    }

    fun populateinfo()
    {  //HERE WE ARE PULLING THE VALUES
        progresslayout.visibility=View.VISIBLE

        userDatabase.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progresslayout.visibility=View.GONE

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (isAdded)
                {
                    val user =p0.getValue(User::class.java)
                    nameET.setText(user?.name,TextView.BufferType.EDITABLE)
                    EmailET.setText(user?.email,TextView.BufferType.EDITABLE)
                    ageET.setText(user?.age,TextView.BufferType.EDITABLE)
                    aboutET.setText(user?.about,TextView.BufferType.EDITABLE) // my idea
                    if (user?.gender == GENDER_MALE)
                    {
                        radioMan1.isChecked=true

                    }
                    if (user?.gender== GENDER_FEMALE)
                    {
                        radioWoman1.isChecked=true
                    }
                    if (user?.preferedgender== GENDER_MALE)
                    {
                        radioMan2.isChecked=true
                    }
                    if (user?.preferedgender== GENDER_FEMALE)
                    {
                        radioWoman2.isChecked=true
                    }
                    if (!user?.imageUrl.isNullOrEmpty() )
                    {
                        populateImage(user?.imageUrl!!)
                    }
                    progresslayout.visibility = View.GONE
                }

            }
        })

    }


    fun onApply()
    {
        //HERE WE ARE PUSHING THE VALUES
        if(nameET.text.toString().isNullOrEmpty()||EmailET.text.toString().isNullOrEmpty()||radioGroup1.checkedRadioButtonId==-1||radioGroup2.checkedRadioButtonId==-1|| aboutET.text.toString().isNullOrEmpty())
        {
            Toast.makeText(context,"Please Complete Your Profile",Toast.LENGTH_SHORT).show()
        }
        else{
            val name =nameET.text.toString()
            val email =EmailET.text.toString()
            val age =ageET.text.toString()
            val about =aboutET.text.toString() // my idea made a change in User Class
            val gender =
                    if (radioMan1.isChecked) GENDER_MALE
            else GENDER_FEMALE

            val preferredgender =
                    if (radioMan2.isChecked) GENDER_MALE
            else GENDER_FEMALE


            userDatabase.child(DATA_NAME).setValue(name)
            userDatabase.child(DATA_EMAIL).setValue(email)
            userDatabase.child(DATA_AGE).setValue(age)
            userDatabase.child(DATA_GENDER).setValue(gender)
            userDatabase.child(DATA_GENDERPREFFERENCE).setValue(preferredgender)
            userDatabase.child(DATA_ABOUT).setValue(about) // myidea made a change in constansts

            callback?.profileComplete()


        }

    }
   fun updateImageUri(uri:String)
   {
       userDatabase.child(DATA_IMAGEURL).setValue(uri)
       populateImage(uri)

   }

    fun populateImage(uri:String)
    {
        Glide.with(this)
            .load(uri).into(photoIV)


    }


}
