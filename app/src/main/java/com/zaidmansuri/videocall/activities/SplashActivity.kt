package com.zaidmansuri.videocall.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zaidmansuri.videocall.R
import com.zaidmansuri.videocall.auth.SignupActivity
import com.zaidmansuri.videocall.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        Glide.with(this).asGif().load(R.drawable.splash_loading).into(binding.logo)
        mAuth = FirebaseAuth.getInstance()


        binding.joinBtn.setOnClickListener {
            if (mAuth.currentUser != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, SignupActivity::class.java))
                finish()
            }
        }


    }
}