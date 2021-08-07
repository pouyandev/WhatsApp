
package com.example.whatsapp.Adapter

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.Model.Chats
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
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
        var img_profile_receiver: CircleImageView? = null
        var fromUserId: String? = null


        fun bindView(chat:Chats) {
            txt_message = itemView.findViewById(R.id.txt_message)
            img_profile_receiver = itemView.findViewById(R.id.img_profile_receiver)

             fromUserId = chat.from!!
           val firebaseAuth = FirebaseAuth.getInstance()
            val messageSenderId: String = firebaseAuth.currentUser!!.uid


            if (fromUserId.equals(messageSenderId)) {
                txt_message!!.setBackgroundResource(R.drawable.message_sender_background)
                txt_message!!.setTextColor(Color.WHITE)
                txt_message!!.gravity = Gravity.END
                img_profile_receiver!!.visibility = View.GONE
            } else {
                txt_message!!.setBackgroundResource(R.drawable.message_receive_background)
                txt_message!!.setTextColor(Color.WHITE)
                txt_message!!.gravity = Gravity.START
                img_profile_receiver!!.visibility = View.VISIBLE
            }
            txt_message!!.text = chat.message

        }
    }
}