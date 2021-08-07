package com.example.whatsapp.Activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.Adapter.ChatsAdapter
import com.example.whatsapp.Model.Chats
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    var mFireDatabaseRef: DatabaseReference? = null
    var mAuth: FirebaseAuth? = null
    var mLinearLayoutManager: LinearLayoutManager? = null
    var messageReceiverId: String? = null
    var messageReceiverName: String? = null
    var messageSenderId: String? = null
    var chats: ArrayList<Chats> = ArrayList()
    var adapter: ChatsAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initViews()

    }

    private fun initViews() {

        mFireDatabaseRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        messageSenderId = mAuth!!.currentUser!!.uid




        messageReceiverId = intent.extras!!.getString("userId")
        messageReceiverName = intent.extras!!.getString("name")
        mLinearLayoutManager = LinearLayoutManager(this)
        rcl_chat_activity.layoutManager = mLinearLayoutManager
        rcl_chat_activity.setHasFixedSize(true)
        adapter = ChatsAdapter(chats)
        rcl_chat_activity.adapter = adapter
        fetchMessage()


        mFireDatabaseRef!!.child("Users").child(messageReceiverId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    var imageUrl = data.child("image").value.toString()
                    var displayName = data.child("display_name").value.toString()

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
            Toast.makeText(this, "Please Enter Your Message ", Toast.LENGTH_LONG).show()
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
            messageTextBody.put("from", messageSenderId!!)


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
                  /*  var message = snapshot.getValue(Chats::class.java)
                    chats!!.add(message!!)
                    adapter!!.notifyDataSetChanged()*/
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


}
/*  userId = intent.extras!!.getString("userId")
  mFirebaseUser = FirebaseAuth.getInstance().currentUser
  mFireDatabaseRef = FirebaseDatabase.getInstance().reference
  initViews()

}

private fun initViews() {

      txt_name_chat_activity.text = intent.extras!!.getString("name")
      mLinearLayoutManager = LinearLayoutManager(this)
      rcl_chat_activity.layoutManager = mLinearLayoutManager
      mLinearLayoutManager!!.stackFromEnd = true


      var chatsQuery = mFireDatabaseRef!!.child("Messages")
          .limitToLast(50)


      var options: FirebaseRecyclerOptions<Chats> =
          FirebaseRecyclerOptions.Builder<Chats>()
              .setQuery(chatsQuery, object : SnapshotParser<Chats?> {
                  override fun parseSnapshot(snapshot: DataSnapshot): Chats {
                      return Chats(snapshot.child("id").value.toString(),
                          snapshot.child("name").value.toString(),
                          snapshot.child("text").value.toString())


                  }
              })
              .build()
      mFirebaseAdapter = object : FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(options) {
          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
              return ChatsViewHolder(LayoutInflater.from(this@ChatActivity)
                  .inflate(R.layout.item_message, parent, false))
          }

          override fun onBindViewHolder(
              holder: ChatsViewHolder,
              position: Int,
              chats: Chats, ) {
              if (chats.form != null) {
                  holder.bindView(chats)

                  var currentUserId = mFirebaseUser!!.uid
                  var isMe = chats.message.equals(currentUserId)

                  if (isMe) {
                      holder.card_message_sender!!.visibility = View.VISIBLE
                      holder.card_message_receiver!!.visibility = View.INVISIBLE
                      holder.txt_message_sender!!.gravity =
                          (Gravity.CENTER_VERTICAL or Gravity.RIGHT)

                      mFireDatabaseRef!!.child("Users").child(currentUserId)
                          .addValueEventListener(object : ValueEventListener {
                              override fun onDataChange(data: DataSnapshot) {
                                  var imageUrl = data.child("thumb_image").value.toString()
                                  var displayName =
                                      data.child("display_name").value.toString()
                              }

                              override fun onCancelled(error: DatabaseError) {
                                  Log.i(TAG, "onCancelled: ${error.message}")
                              }
                          })
                  } else {
                      holder.card_message_sender!!.visibility = View.INVISIBLE
                      holder.card_message_receiver!!.visibility = View.VISIBLE
                      holder.txt_message_receiver!!.gravity =
                          (Gravity.CENTER_VERTICAL or Gravity.LEFT)

                      mFireDatabaseRef!!.child("Users").child(userId!!)
                          .addValueEventListener(object : ValueEventListener {
                              override fun onDataChange(data: DataSnapshot) {
                                  var imageUrl = data.child("thumb_image").value.toString()
                                  var displayName =
                                      data.child("display_name").value.toString()

                                  Picasso.get().load(imageUrl)
                                      .into(holder.img_profile_receiver)


                              }

                              override fun onCancelled(error: DatabaseError) {
                                  Log.i(TAG, "onCancelled: ${error.message}")
                              }
                          })
                  }


              }


          }
      }

  rcl_chat_activity.adapter = mFirebaseAdapter



  fab_chat_activity.setOnClickListener {
      if (intent.extras!!.getString("name")!! != "") {
          var currentUserName = intent.extras!!.getString("name")
          var mCurrentUserId = mFirebaseUser!!.uid

          var chats: Chats = Chats(mCurrentUserId,
              currentUserName!!.toString().trim(),
              edt_send_message_chat_activity.getText().toString())

          mFireDatabaseRef!!.child("messages").push().setValue(chats)
          edt_send_message_chat_activity.text=null
      }

      img_back_chat_activity.setOnClickListener {
          onBackPressed()
      }

  }


}
}*/


/*        initViews()
        fab_chat_activity.setOnClickListener(this)
        img_back_chat_activity.setOnClickListener(this)


    }

    private fun initViews() {
        mLinearLayoutManager = LinearLayoutManager(this)
        rcl_chat_activity.layoutManager = mLinearLayoutManager
        messages = ArrayList()
        userId = intent.extras!!.getString("userId")
        mCurrentUser = FirebaseAuth.getInstance().currentUser
        mFireDatabaseRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
        mFireDatabaseRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var users: Users = dataSnapshot.getValue(Users::class.java)!!
                txt_name_chat_activity.text = users.display_name
                if (users.image.equals("default")) {
                    img_profile_receiver.setImageResource(R.drawable.placeholder)
                } else {
                    // Picasso.get().load(users.image).into(img_profile_receiver)
                }
                readMessage(mCurrentUser!!.uid, userId!!, users.thumb_image!!)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun readMessage(myId: String, userId: String, imgUrl: String) {
        messages = ArrayList()
        mFireDatabaseRef = FirebaseDatabase.getInstance().reference.child("Chats")
        mFireDatabaseRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    var message: FriendlyMessage = snapshot.getValue(FriendlyMessage::class.java)!!
                    if (message.text.equals(myId) && message.name.equals(userId) ||
                        message.text.equals(userId) && message.name.equals(myId)
                    ) {
                        messages!!.add(message)
                    }

                    adapter = ChatsAdapter(messages!!, imgUrl)
                    rcl_chat_activity.adapter = adapter
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun sendMessage(id: String, name: String, text: String) {
        *//*      mFireDatabaseRef = FirebaseDatabase.getInstance().reference*//*

        var map = HashMap<String, String>()
        map.put("id", id)
        map.put("sender", name)
        map.put("receiver", text)
        mFireDatabaseRef!!.push().setValue(map)
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            fab_chat_activity.id -> {

                    var msg = edt_send_message_chat_activity.text.toString().trim()
                    if (!msg.equals("")) {
                        sendMessage(mCurrentUser!!.uid, userId!!, msg)
                    }

                edt_send_message_chat_activity.setText("")

            }
            img_back_chat_activity.id->{
                onBackPressed()
            }
        }
    }*/


