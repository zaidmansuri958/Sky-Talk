package com.zaidmansuri.videocall.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.zaidmansuri.videocall.activities.MainActivity
import com.zaidmansuri.videocall.databinding.ActivityOtpactivityBinding
import com.zaidmansuri.videocall.model.User
import com.zaidmansuri.videocall.utils.config
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpactivityBinding
    private var verificationID: String? = null
    val auth = FirebaseAuth.getInstance()
    private var name: String = ""
    private var email: String = ""
    private var password: String = ""
    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#2f2e41")))
        supportActionBar!!.title="Authentication"
        val intent = intent
        name = intent.getStringExtra("name").toString()
        email = intent.getStringExtra("email").toString()
        password = intent.getStringExtra("password").toString()
        image = intent.getStringExtra("image").toString()



        binding.btnSendotp.setOnClickListener {
            config.showDialog(this)
            if (binding.userNumber.text!!.isEmpty()) {
                binding.userNumber.error = "Please Enter Phone Number"
            } else {
                sendOTP(binding.userNumber.text.toString())
            }
        }

        binding.btnVerify.setOnClickListener {
            config.showDialog(this)
            if (binding.userOtp.text!!.isEmpty()) {
                binding.userOtp.error = "Please Enter OTP Number"
            } else {
                verifyOTP(binding.userOtp.text.toString())
            }
        }
    }

    private fun sendOTP(number: String) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                config.dismissDialog()
                Log.d("MYTAG", e.toString())
                Toast.makeText(this@OTPActivity, e.toString(), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                config.dismissDialog()
                this@OTPActivity.verificationID = verificationId
                binding.numberLayout.visibility = View.GONE
                binding.otpLayout.visibility = View.VISIBLE
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$number")       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.currentUser!!.linkWithCredential(credential).addOnSuccessListener {
            Toast.makeText(this, "account created successfully ", Toast.LENGTH_SHORT).show()
            storeData()
        }.addOnFailureListener {
            config.dismissDialog()
            Log.d("MYTAG",it.message.toString())
            Toast.makeText(this,it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyOTP(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationID!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun storeData() {
        val user = User(name, email, binding.userNumber.text.toString(), password, null, image)
        FirebaseDatabase.getInstance().getReference("users")
            .child(auth.currentUser!!.uid)
            .setValue(user).addOnCompleteListener {
                if (it.isSuccessful) {
                    config.dismissDialog()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    config.dismissDialog()
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                    Log.d("TAG", it.toString())
                }
            }
    }
}