package com.example.whatsapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import com.example.whatsapp.Adapter.SectionPagerAdapter
import com.example.whatsapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(),
    View.OnClickListener {
    var sectionAdapter: SectionPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        var intent = intent.extras
        Toast.makeText(this, intent!!.getString("name").toString(), Toast.LENGTH_LONG).show()
        initViews()
        sectionAdapter = SectionPagerAdapter(supportFragmentManager)
        view_pager_dashboard.adapter = sectionAdapter
        main_tab.setupWithViewPager(view_pager_dashboard)


    }

    private fun initViews() {
        img_popup.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            img_popup.id -> showPopup(img_popup)
        }
    }

    private fun showPopup(v:View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings_id -> {
                    startActivity(Intent(this,
                        SettingsActivity::class.java))
                    true
                }
                R.id.logout_id -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this.applicationContext,
                        MainActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
        popup.inflate(R.menu.main_menu)
        popup.show()
    }


}