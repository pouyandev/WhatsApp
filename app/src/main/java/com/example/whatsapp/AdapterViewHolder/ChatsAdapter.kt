package com.example.whatsapp.AdapterViewHolder

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.Model.Chats
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private val chats: ArrayList<Chats>) :
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    var firebaseAuth: FirebaseAuth? = null
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int, ): ChatsViewHolder {
        firebaseAuth = FirebaseAuth.getInstance()
        return ChatsViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false))

    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bindView(chats[position])


    }

    override fun getItemCount(): Int {
        return chats.count()
    }


    class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txt_message: TextView? = null
        var txt_my_message: TextView? = null
        var fromUserId: String? = null
        var mFirebaseDatabase:DatabaseReference?=null


        fun bindView(chat: Chats) {
            fromUserId = chat.from!!
            txt_message = itemView.findViewById(R.id.txt_message)
            txt_my_message = itemView.findViewById(R.id.txt_my_message)
            mFirebaseDatabase=FirebaseDatabase.getInstance().reference




            val firebaseAuth = FirebaseAuth.getInstance()
            val messageSenderId: String = firebaseAuth.currentUser!!.uid


            if (fromUserId.equals(messageSenderId)) {
                txt_message!!.visibility = View.GONE
                txt_my_message!!.visibility=View.VISIBLE
            } else {
                txt_message!!.visibility = View.VISIBLE
                txt_my_message!!.visibility=View.GONE
            }
            txt_message!!.text = chat.message
            txt_my_message!!.text = chat.message


        }
    }
}