package com.example.foundler.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.foundler.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }

    fun onSignup(v: View){
        startActivity(MainActivity.newIntent(this))
    }

    companion object {
        fun newIntent(context: Context?) = Intent(context, SignupActivity::class.java)
    }
}
