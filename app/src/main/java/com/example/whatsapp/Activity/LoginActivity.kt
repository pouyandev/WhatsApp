package com.example.whatsapp.Activity


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    var mAuth: FirebaseAuth? = null
    var mDatabase: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initViews()
    }

    private fun initViews() {
        mAuth = FirebaseAuth.getInstance()
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            btn_login.id -> {
                var email=edt_email_login.text.toString().trim()
                var password=edt_pass_login.text.toString().trim()
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    loginUser(email, password)
                }else{
                    Toast.makeText(this, "Please Enter Your Information", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun loginUser(email: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult>->

                if (task.isSuccessful){
                    var dashboardIntent = Intent(this, DashboardActivity::class.java)
                    var userName=email.split("@")[0]
                    dashboardIntent.putExtra("name",userName)
                    startActivity(dashboardIntent)
                    finish()
                }else{
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                }
            }
    }
}