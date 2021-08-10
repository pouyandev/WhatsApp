package com.example.whatsapp.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.Activity.ChatActivity
import com.example.whatsapp.Activity.ProfileActivity
import com.example.whatsapp.AdapterViewHolder.UsersViewHolder
import com.example.whatsapp.Model.Users
import com.example.whatsapp.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.bottom_sheet_view.*
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {
    var mUserDatabase: DatabaseReference? = null
    lateinit var adapter: FirebaseRecyclerAdapter<Users, UsersViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        var linearLayoutManager = LinearLayoutManager(context)
        rcl_users_fragment.setHasFixedSize(true)
        rcl_users_fragment.layoutManager = linearLayoutManager
        fetch()


    }
    /*  private fun showAlertDialog(){
          var options = arrayOf("Open Profile ", "Send Message")
          var builder = AlertDialog.Builder(context)
          builder.setTitle("Select Option")
          builder.setItems(options,
              DialogInterface.OnClickListener { dialogDialogInterface: DialogInterface?, i: Int ->
                  var userName = holder.txt_name_profile_row
                  var userStatus = holder.txt_status_profile_row
                  var profilePic = holder.userProfilePicLink

                  if (i == 0) {
                      //Open Profile Activity

                      var profileIntent = Intent(context, ProfileActivity::class.java)
                      profileIntent.putExtra("userId",userId)
                      context!!.startActivity(profileIntent)
                  } else {
                      //Open Chat Activity
                      var chatIntent = Intent(context, ChatActivity::class.java)
                      chatIntent.putExtra("userId",userId)
                      chatIntent.putExtra("name",userName)
                      chatIntent.putExtra("status",userStatus)
                      chatIntent.putExtra("image",profilePic)
                      context!!.startActivity(chatIntent)


                  }
              })
          builder.show()
      }*/

    private fun fetch() {
        var usersQuery = FirebaseDatabase.getInstance()
            .reference
            .child("Users")
            .limitToLast(50)

        var options: FirebaseRecyclerOptions<Users> =
            FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(usersQuery, object : SnapshotParser<Users?> {
                    override fun parseSnapshot(snapshot: DataSnapshot): Users {
                        return Users(snapshot.child("display_name").value.toString(),
                            snapshot.child("image").value.toString(),
                            snapshot.child("thumb_image").value.toString(),
                            snapshot.child("status").value.toString())
                    }
                })
                .build()
        adapter =
            object : FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): UsersViewHolder {
                    return UsersViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.users_row, parent, false))
                }

                protected override fun onBindViewHolder(
                    holder: UsersViewHolder, position: Int, model: Users,
                ) {
                    var userId = getRef(position).key
                    holder.bindView(model)
                    holder.itemView.setOnClickListener {

                            var chatIntent = Intent(context, ChatActivity::class.java)
                            var userName = holder.txt_name_profile_row
                            var userStatus = holder.txt_status_profile_row
                            var profilePic = holder.userProfilePicLink

                            chatIntent.putExtra("userId",userId)
                            chatIntent.putExtra("name",userName)
                            chatIntent.putExtra("status",userStatus)
                            chatIntent.putExtra("image",profilePic)
                            context!!.startActivity(chatIntent)


                    }
                }

                override fun onDataChanged() {

                }
            }
        rcl_users_fragment.adapter = adapter
        adapter.startListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }


}