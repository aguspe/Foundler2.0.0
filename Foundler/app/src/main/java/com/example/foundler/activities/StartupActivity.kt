package com.example.foundler.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.foundler.R

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }

    fun onLogin(v: View){
        startActivity(LoginActivity.newIntent(this))
    }

    fun onSignUp(v: View){
        startActivity(SignupActivity.newIntent(this))
    }
}
