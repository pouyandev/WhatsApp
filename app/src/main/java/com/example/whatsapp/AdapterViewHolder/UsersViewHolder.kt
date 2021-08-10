package com.example.whatsapp.AdapterViewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsapp.Model.Users
import com.example.whatsapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var txt_name_profile_row: String? = null
    var txt_status_profile_row: String? = null
    var userProfilePicLink: String? = null

    fun bindView(user: Users) {
        val userName = itemView.findViewById<TextView>(R.id.txt_name_profile_row)
        val status = itemView.findViewById<TextView>(R.id.txt_status_profile_row)
        val userProfilePic = itemView.findViewById<CircleImageView>(R.id.users_profile_row)!!
        

        txt_name_profile_row = user.display_name
        txt_status_profile_row = user.status
        userProfilePicLink = user.thumb_image

        userName.text = user.display_name
        status.text = user.status
       Picasso.get().load(userProfilePicLink).placeholder(R.drawable.placeholder).into(userProfilePic)

    }


}
