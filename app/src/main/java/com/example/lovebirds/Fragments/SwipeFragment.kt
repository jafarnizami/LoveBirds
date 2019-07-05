package com.example.lovebirds.Fragments


import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.lovebirds.Activities.LoveBirdsCallback

import com.example.lovebirds.R
import com.example.lovebirds.adapters.CardAdapter
import com.example.lovebirds.util.*
import com.google.firebase.database.*
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import kotlinx.android.synthetic.main.fragment_swipe.*


class SwipeFragment : Fragment() {

    private var callback: LoveBirdsCallback?=null
    private lateinit var userId:String
    private lateinit var userDatabase: DatabaseReference
    private lateinit var chatDatabase:DatabaseReference
    private var cardsAdapter:ArrayAdapter<User>?=null
    private var rowItems=ArrayList<User>()
    private var preferredGENDER :String?=null
    private var userName :String?=null
    private var imageUrl:String?=null

    fun setCallback(callback: LoveBirdsCallback)
    {
        this.callback=callback
        userId=callback.onGetUserId()
        userDatabase=callback.getUserDatabase()
        chatDatabase=callback.getChatDatabase()

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_swipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                preferredGENDER = user?.preferedgender
                userName=user?.name
                imageUrl=user?.imageUrl

                populateItems()
            }
        })

        cardsAdapter = CardAdapter(context, R.layout.item, rowItems)


        frame.adapter = cardsAdapter
        frame.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                rowItems.removeAt(0)     // THIS WILL REMOVE THAT CARD FROM THE ARRAY TO WHICH USER HAS SWIPED AND THEN ANOTHER CARD WILL BE LOADED
                cardsAdapter?.notifyDataSetChanged()

            }

            override fun onLeftCardExit(p0: Any?) {  //Im Alice and im Swiping Bob : Left
                var user = p0 as User //p0 is the data of Bob (present in the card)
                userDatabase.child(user.uid.toString()).child(DATA_SWIPES_LEFT).child(userId).setValue(true)  // we will store id of Alice under the child swipes left in Bobs tree...

                //userId is the id of alice.
            }

            override fun onRightCardExit(p0: Any?) { // Im Alice and im swiping Bob:Right

                val Selecteduser = p0 as User
                val SelecteduserId = Selecteduser.uid
                if (!SelecteduserId.isNullOrEmpty())
                {
                    userDatabase.child(userId).child(DATA_SWIPE_RIGHT).addListenerForSingleValueEvent(object :ValueEventListener{ // pulls the data present in the DATA_SWIPE_RIGHT child of Alice
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChild(SelecteduserId)) //CHECKING IF ALICE HAS ID OF BOB OR NOT (MEANS THAT BOB HAS SWIPED HER ALREADY OR NOT)
                            {
                                Toast.makeText(context, "ITS A MATCH", Toast.LENGTH_SHORT).show()

                                val chatKey = chatDatabase.push().key //pushes a key in the chatdatabase of the user

                                if (chatKey != null) {
                                    userDatabase.child(userId).child(DATA_SWIPE_RIGHT).child(SelecteduserId) // since when bob has swiped alice right so bob's id as stored in alice's database so we areremoving that value
                                        .removeValue()
                                    userDatabase.child(userId).child(DATA_MATCHES).child(SelecteduserId)
                                        .setValue(chatKey)
                                    userDatabase.child(SelecteduserId).child(DATA_MATCHES).child(userId)
                                        .setValue(chatKey)

                                    //WE WILL PASS ALL THE DETAILS OF BOTH THE USER INTO THE CHAT DATABASE SO THAT THESE DETAILS CAN BE EXTRACTED FROM THE RECYCLER VIEW..

                                    chatDatabase.child(chatKey).child(userId).child(DATA_NAME).setValue(userName)  // SAVING ALL THR DATA OF THE ALICE in the chatDatabase under "chatKey" child
                                    chatDatabase.child(chatKey).child(userId).child(DATA_IMAGEURL).setValue(imageUrl)

                                    chatDatabase.child(chatKey).child(SelecteduserId).child(DATA_NAME).setValue(Selecteduser.name) // SAVING ALL THE DATA OF THE BOB in the chatDatabase "chatkey" child
                                    chatDatabase.child(chatKey).child(SelecteduserId).child(DATA_IMAGEURL).setValue(Selecteduser.imageUrl)


                                }
                            }
                            //THIS LINE WILL BE CALLED IF BOB HAS NOT SWIPED RIGHT ALICE ALREADY
                                else {
                                    userDatabase.child(SelecteduserId).child(DATA_SWIPE_RIGHT).child(userId).setValue(true)
                                }                                                                                            // IF BOB HAS NOT SWIPED HER RIGHT THEN ALICES ID WILL BE STORED IN CHILD swipesright
                        }
                    })
                }


            }

            override fun onAdapterAboutToEmpty(p0: Int) {
            }

            override fun onScroll(p0: Float) {
            }
        })
        
        frame.setOnItemClickListener { position, data ->  }  // nothing happens when the user clicks on the frame
        likeButton.setOnClickListener {
            if (!rowItems.isEmpty())
            {
                frame.topCardListener.selectRight()
            }
        }

        dislikeButton.setOnClickListener {
            if (!rowItems.isEmpty())
            {
                frame.topCardListener.selectLeft()
            }
        }

    }

    fun populateItems() {
        noUsersLayout.visibility = View.GONE
        progresLayout2.visibility = View.VISIBLE
        val cardsQuery = userDatabase.orderByChild(DATA_GENDER).equalTo(preferredGENDER)
        cardsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { child ->
                    val user = child.getValue(User::class.java)
                    if (user != null) {
                        var showUser = true
                        if (child.child(DATA_SWIPES_LEFT).hasChild(userId) ||
                            child.child(DATA_SWIPE_RIGHT).hasChild(userId) ||
                            child.child(DATA_MATCHES).hasChild(userId)
                        ) {
                            showUser = false
                        }
                        if (showUser) {
                            rowItems.add(user)
                            cardsAdapter?.notifyDataSetChanged()
                        }
                    }
                }
                progresLayout2.visibility = View.GONE
                noUsersLayout.visibility = View.VISIBLE // MY OWN IDEA SO IF ANY PROBLEM OCCURS COME HERE...
                if (rowItems.isEmpty()) {
                    noUsersLayout.visibility = View.VISIBLE
                }
            }
        })
    }

}



