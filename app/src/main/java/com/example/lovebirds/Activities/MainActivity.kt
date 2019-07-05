package com.example.lovebirds.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lovebirds.Fragments.MatchesFragmentt
import com.example.lovebirds.Fragments.ProfileFragment
import com.example.lovebirds.Fragments.SwipeFragment
import com.example.lovebirds.R
import com.example.lovebirds.util.DATA_CHATS
import com.example.lovebirds.util.DATA_USERS
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQUEST_CODE_PHOTO=1234

//ADD THE  implementation 'com.lorentzos.swipecards:library:1.0.9'(SWIPE CARD DEPENDECY)
//ACTIVITY TO MAKE THE SWIPE GESTURE IN MY APP

class MainActivity : AppCompatActivity(),LoveBirdsCallback {

    private val firebaseAuth =FirebaseAuth.getInstance()
    private val userId=firebaseAuth.currentUser?.uid
    private lateinit var  userDatabase :DatabaseReference
    private lateinit var  chatDatabase :DatabaseReference


    private var profileFragment:ProfileFragment ?=null //making objects of the fragments
    private var swipeFragment:SwipeFragment ?=null
    private var matchesFragmentt:MatchesFragmentt?=null

    private var profileTab :TabLayout.Tab?=null
    private var swipeTab :TabLayout.Tab?=null
    private var matchesTab:TabLayout.Tab?=null
    private var resultImageUrl :Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (userId.isNullOrEmpty())
        {
            onSignout()
        }

        userDatabase =FirebaseDatabase.getInstance().reference.child(DATA_USERS) // this is defined here only once and can be used in the whole app using setcallback fumc
        chatDatabase=FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        profileTab =navigationTabs.newTab() // this function adds new tabs in the navigationTabs which we made in the main activity xml
        swipeTab =navigationTabs.newTab()
        matchesTab =navigationTabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_swipe)
        matchesTab?.icon =ContextCompat.getDrawable(this,R.drawable.tab_matches)


        navigationTabs.addTab(profileTab!!)
        navigationTabs.addTab(swipeTab!!)
        navigationTabs.addTab(matchesTab!!)

        navigationTabs.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {
                onTabSelected(p0)

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when(p0)
                {
                    profileTab->
                    {
                        if (profileFragment==null)
                        {
                            profileFragment= ProfileFragment()
                            profileFragment!!.setCallback(this@MainActivity)  //setcallback function int e fragment will be called first
                        }

                        replaceFragment(profileFragment!!)

                    }
                    swipeTab->{
                        if (swipeFragment==null)
                        {
                            swipeFragment = SwipeFragment()
                            swipeFragment!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(swipeFragment!!) // i put SwipeFragment here by mistake and omg i was stuck on it for 3 hours.....

                    }
                    matchesTab->{
                        if (matchesFragmentt==null)
                        {
                            matchesFragmentt = MatchesFragmentt()
                            matchesFragmentt!!.setCallback(this@MainActivity)
                        }
                        replaceFragment(matchesFragmentt!!)


                    }
                }

            }

        })
        profileTab?.select() // when we open the main activity the profile fragment will be automatically selected...
}
    fun replaceFragment(fragment:Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.fragmentcontainer,fragment).commit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode==Activity.RESULT_OK && requestCode== REQUEST_CODE_PHOTO )
        {
            resultImageUrl= data?.data
            storeImage()

        }
    }

    fun storeImage()
    {
        if (resultImageUrl!=null && userId!=null)
        {
            val filepath =FirebaseStorage.getInstance().getReference("profileImage").child(userId)
            var bitmap:Bitmap?=null
            try {
                bitmap=MediaStore.Images.Media.getBitmap(application.contentResolver,resultImageUrl)
            }
            catch (e:IOException){
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()// creates a new byte array output stream initial capacity 32 bytes..
            bitmap?.compress(Bitmap.CompressFormat.JPEG,20,baos)
            val data = baos.toByteArray()
            //image will be in a form of byte array
            val uploadTask=filepath.putBytes(data)
            uploadTask.addOnFailureListener { e->e.printStackTrace() }
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filepath.downloadUrl
                    .addOnSuccessListener {uri ->
                        profileFragment?.updateImageUri(uri.toString())
                    }
                    .addOnFailureListener{e->e.printStackTrace()}
            }
        }
    }
    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartActivity.newIntent(this))
        finish()

    }

    override fun onGetUserId(): String =userId!!


    override fun getUserDatabase(): DatabaseReference  = userDatabase

    override fun getChatDatabase(): DatabaseReference =chatDatabase
    override fun profileComplete() {
        swipeTab?.select()
    }

    override fun startActivityForPhoto() {
        val intent =Intent(Intent.ACTION_PICK)
        intent.type ="image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }


    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

}
