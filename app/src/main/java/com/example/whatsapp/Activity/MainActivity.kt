package com.example.whatsapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    var mAuth: FirebaseAuth? = null
    var user: FirebaseUser? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth= FirebaseAuth.getInstance()
        mAuthListener=FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
            user=firebaseAuth.currentUser
            if (user!=null){
                var dashboardIntent = Intent(this, DashboardActivity::class.java)
                val userName=user!!.email!!.split("@")[0]
                dashboardIntent.putExtra("name",userName)
                startActivity(dashboardIntent)
                finish()
            }
        }
        btn_have_account.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        btn_new_account.setOnClickListener {
            startActivity(Intent(this,CreateAccountActivity::class.java))
        }



    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()

        if (mAuthListener!=null){
            mAuth!!.removeAuthStateListener (mAuthListener!!)
        }

    }

}

