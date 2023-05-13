package com.zaidmansuri.videocall.auth

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.zaidmansuri.videocall.databinding.ActivitySignupBinding
import com.zaidmansuri.videocall.utils.config


class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var imageUri: Uri? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it
        binding.userImg.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#2f2e41")))
        supportActionBar!!.title="Register"
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        binding.userImg.setOnClickListener {
            selectImage.launch("image/*")
        }

        binding.btnSignup.setOnClickListener {
            config.showDialog(this)
            validateData()
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }

    private fun uploadImage() {

        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(binding.userEmail.text.toString()).child("profile.jpg")

        storageRef.putFile(imageUri!!).addOnSuccessListener {
            config.dismissDialog()
            storageRef.downloadUrl.addOnSuccessListener {
                val intent = Intent(this, OTPActivity::class.java)
                intent.putExtra("name", binding.userName.text.toString())
                intent.putExtra("email", binding.userEmail.text.toString())
                intent.putExtra("password", binding.userPassword.text.toString())
                intent.putExtra("image", it.toString())
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Log.d("TAG", it.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            Log.d("TAG", it.toString())
        }
    }

    private fun signup() {
        mAuth = FirebaseAuth.getInstance()
        mAuth.createUserWithEmailAndPassword(
            binding.userEmail.text.toString(),
            binding.userPassword.text.toString()
        ).addOnSuccessListener {
            uploadImage()
        }.addOnFailureListener {
            config.dismissDialog()
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateData() {
        if (binding.userName.text.toString().isEmpty() || binding.userEmail.text.toString()
                .isEmpty() || imageUri == null || binding.userPassword.toString().isEmpty()
        ) {
            config.dismissDialog()
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
        } else {
            signup()
        }
    }

}