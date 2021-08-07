package com.example.whatsapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    var mDatabase: DatabaseReference? = null
    var mCurrentuser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initViews()
        val userId=intent.extras!!.getString("userId")
        mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!)
        mCurrentuser = FirebaseAuth.getInstance().currentUser
        setUpProfile()



    }

    private fun initViews() {
        btn_send_message_profile_activity.setOnClickListener(this)
    }

    private fun setUpProfile() {
        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                var name=datasnapshot.child("display_name").value.toString()
                var status=datasnapshot.child("status").value.toString()
                var image=datasnapshot.child("image").value.toString()

                txt_name_profile_activity.text=name
                txt_status_profile_activity.text=status
                Picasso.get().load(image).placeholder(R.drawable.placeholder).into(img_profile_activity)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            btn_send_message_profile_activity.id->{
                val chatIntent= Intent(this@ProfileActivity,ChatActivity::class.java)
                startActivity(chatIntent)
            }
        }
    }
}