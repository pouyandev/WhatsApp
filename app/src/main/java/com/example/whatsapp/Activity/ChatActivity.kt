package com.example.whatsapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.AdapterViewHolder.ChatsAdapter
import com.example.whatsapp.Model.Chats
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity(), View.OnClickListener {


    private var userId: String?=null
    private var profileStatus: String?=null
    private var displayName: String?=null
    private val TAG = "ChatActivity"
    var mFireDatabaseRef: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var messageReceiverId: String? = null
    var messageReceiverName: String? = null
    var messageSenderId: String? = null
    var chats: ArrayList<Chats> = ArrayList()
    var adapter: ChatsAdapter? = null
    var messageSenderName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initViews()

    }

    private fun initViews() {

        mFireDatabaseRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        messageSenderId = mAuth!!.currentUser!!.uid
        root_to_profile_chat_activity.setOnClickListener(this)





        messageReceiverId = intent.extras!!.getString("userId")
        messageReceiverName = intent.extras!!.getString("name")
        profileStatus = intent.extras!!.getString("status")
        mLinearLayoutManager = LinearLayoutManager(this)
        rcl_chat_activity.layoutManager = mLinearLayoutManager
        mLinearLayoutManager!!.stackFromEnd = true
        rcl_chat_activity.setHasFixedSize(true)
        adapter = ChatsAdapter(chats)
        rcl_chat_activity.adapter = adapter
        fetchMessage()


        mFireDatabaseRef!!.child("Users").child(messageReceiverId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    var imageUrl = data.child("thumb_image").value.toString()
                      displayName = data.child("display_name").value.toString()

                    Picasso.get().load(imageUrl).placeholder(R.drawable.placeholder)
                        .into(img_profile_chat_activity)
                    txt_name_chat_activity.text = displayName
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        fab_chat_activity.setOnClickListener {
            sendMessage()
        }

        img_back_chat_activity.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMessage() {
        var messageText = edt_send_message_chat_activity.text.toString()
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please Enter Your Message ", Toast.LENGTH_SHORT).show()
        } else {
            var message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId
            var message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId


            var user_message_key: DatabaseReference = mFireDatabaseRef!!
                .child("Messages").child(messageSenderId!!)
                .child(messageReceiverId!!).push()

            var message_push_id = user_message_key.key

            var messageTextBody = HashMap<Any, Any>()
            messageTextBody.put("message", messageText)
            messageTextBody.put("type", "text")
            messageTextBody.put("time", ServerValue.TIMESTAMP)
            messageTextBody.put("from",messageSenderId!!)
            messageTextBody.put("to", displayName!!)


            var messageBodyDetail = HashMap<String, Any>()
            messageBodyDetail.put(message_sender_ref + "/" + message_push_id, messageTextBody)
            messageBodyDetail.put(message_receiver_ref + "/" + message_push_id, messageTextBody)

            mFireDatabaseRef!!.updateChildren(messageBodyDetail,
                DatabaseReference.CompletionListener { error, ref ->

                    if (error != null) {
                        Log.d(TAG, "initViews: ${error.message}")
                    }
                    edt_send_message_chat_activity.setText("")
                })


        }
    }

    private fun fetchMessage() {

        mFireDatabaseRef!!.child("Messages").child(messageSenderId!!).child(messageReceiverId!!)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var message = snapshot.getValue(Chats::class.java)
                    chats.add(message!!)
                    adapter!!.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            root_to_profile_chat_activity.id->{
                var profileIntent = Intent(this, ProfileActivity::class.java)
                profileIntent.putExtra("userId",messageReceiverId)
  
                startActivity(profileIntent)
            }
        }
    }


}