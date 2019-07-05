package com.example.lovebirds.Fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lovebirds.Activities.LoveBirdsCallback

import com.example.lovebirds.R
import com.example.lovebirds.adapters.ChatsAdapter
import com.example.lovebirds.util.Chat
import com.example.lovebirds.util.DATA_MATCHES
import com.example.lovebirds.util.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_matches_fragmentt.*


class MatchesFragmentt : Fragment() {

    private lateinit var userId:String
    private lateinit var userDatabase:DatabaseReference
    private lateinit var charDatabase:DatabaseReference
    private val chatsAdapter =ChatsAdapter(ArrayList())

    private var callback: LoveBirdsCallback?=null


    fun setCallback(callback: LoveBirdsCallback)  // this function will be called first as it is declared with the fragment in the main activity
    {
        this.callback=callback
        userId=callback.onGetUserId()
        userDatabase=callback.getUserDatabase()
        charDatabase=callback.getChatDatabase()

        fetchData()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_matches_fragmentt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRV.apply {

            setHasFixedSize(false)
            layoutManager=LinearLayoutManager(context)
            adapter=chatsAdapter
        }
    }



    fun fetchData()
    {
        userDatabase.child(userId).child(DATA_MATCHES).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren())
                {
                    p0.children.forEach {child ->
                        val matchId =child.key // THIS STATEMENT GIVES THE REFERENCE TO THE USER THAT IS STORED IN THE CHATDATABASE , THE KEY THAT WE CREATED WHEN SWIPING RIGHT THAT SAME KEY IS CALLED HERE
                        //KEYS CREATED IN SWIPE FRAGMENT LINE 111 AND LINE 108
                        val chatId = child.value.toString()
                        if (!matchId.isNullOrEmpty())
                        {
                            userDatabase.child(matchId).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user =p0.getValue(User::class.java)
                                    if (user!=null)
                                    {
                                        val chat =Chat(userId,chatId,user.uid,user.name,user.imageUrl)
                                        chatsAdapter.addElement(chat)    //SENDING THE DATA TO THE RECYCLER VIEW ADAPTER

                                    }
                                }
                            })
                        }


                    }

                }
            }
        })
    }

}
