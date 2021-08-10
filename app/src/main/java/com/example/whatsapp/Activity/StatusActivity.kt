
package com.example.whatsapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.whatsapp.Constanse
import com.example.whatsapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity(), View.OnClickListener {
    var mDatabase: DatabaseReference? = null
    var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        initViews()


    }

    private fun initViews() {
        if (intent.extras != null) {
            val intent = intent.extras
            val oldStatus = intent!!.getString(Constanse.STATUS)
            edt_status_activity.setText(oldStatus)
        } else if (intent.extras == null) {
            txt_status_activity.text = "Enter Your New Status"
        } else {
            txt_status_activity.text = "Change Status"
        }

        btn_save_status_activity.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_save_status_activity.id -> {
                currentUser=FirebaseAuth.getInstance().currentUser
                val userId=currentUser!!.uid
                mDatabase=FirebaseDatabase.getInstance().reference
                    .child("Users").child(userId)

                var status=edt_status_activity.text.toString().trim()
                mDatabase!!.child("status").setValue(status)
                    .addOnCompleteListener {task:Task<Void>->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Status Updated Successfully",Toast.LENGTH_LONG).show()
                           /* startActivity(Intent(this,SettingsActivity::class.java))*/
                        }else{
                            Toast.makeText(this,"Status Changed Successfully",Toast.LENGTH_LONG).show()

                        }
                    }
                finish()
            }
        }
    }


}