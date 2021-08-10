package com.example.whatsapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.whatsapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {
    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        initViews()


    }

    private fun initViews() {
        mAuth = FirebaseAuth.getInstance()
        btn_create.setOnClickListener(this)
    }

    fun createAccount(email: String, password: String, displayName: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    var currentUser = mAuth!!.currentUser
                    var userId = currentUser!!.uid
                    mDatabase = FirebaseDatabase.getInstance().reference
                        .child("Users").child(userId)

                    var userObjects = HashMap<String, Any>()
                    userObjects.put("display_name", displayName)
                    userObjects.put("status", "Hey there i am using HiApp ")
                    userObjects.put("image", "default")
                    userObjects.put("thumb_image", "default")
                    userObjects.put("online", false)


                    mDatabase!!.setValue(userObjects).addOnCompleteListener { task: Task<Void> ->
                        if (task.isSuccessful) {
                            var dashboardIntent = Intent(this, DashboardActivity::class.java)
                            dashboardIntent.putExtra("name",displayName)
                            startActivity(dashboardIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "User Not Created ", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "There Is A User With This Email", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            btn_create.id -> {
                // v as Button
                var email = edt_email_create.text.toString().trim()
                var password = edt_pass_create.text.toString().trim()
                var displayName = edt_name_create.text.toString().trim()

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(
                        displayName)
                ) {
                    createAccount(email, password, displayName)
                } else {
                    Toast.makeText(this, "Please Enter Your Information ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}