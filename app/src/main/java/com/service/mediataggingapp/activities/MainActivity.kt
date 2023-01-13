package com.service.mediataggingapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.service.mediataggingapp.R
import com.service.mediataggingapp.utils.InitApplication
import com.service.mediataggingapp.utils.NetworkCheck.isConnected


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @BindView(R.id.logout)
    lateinit var logout: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val init = InitApplication(this)
        if (init.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        initComponent()
        startAction()

    }

    private fun initComponent() {
        auth = FirebaseAuth.getInstance();
    }

    private fun startAction() {
        logout.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showNoInternetDialog() {

    }

    override fun onStart() {
        super.onStart()
        if (isConnected(this)) {
            val user = auth.currentUser
            if (user == null) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            showNoInternetDialog()
        }
    }
}

