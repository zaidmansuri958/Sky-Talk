package com.zaidmansuri.videocall.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.zaidmansuri.videocall.activities.MainActivity
import com.zaidmansuri.videocall.databinding.ActivityLoginBinding
import com.zaidmansuri.videocall.utils.config

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#2f2e41")))
        supportActionBar!!.title="Login"

        binding.btnLogin.setOnClickListener {
            config.showDialog(this)
            mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithEmailAndPassword(
                binding.userEmail.text.toString(),
                binding.userPassword.text.toString()
            ).addOnCompleteListener {
                config.dismissDialog()
                if (it.isSuccessful()) {
                    Toast.makeText(this@LoginActivity, "Login Successfully", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please provide valid credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.signup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}