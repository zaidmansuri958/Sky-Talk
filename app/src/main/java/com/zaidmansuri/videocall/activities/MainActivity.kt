package com.zaidmansuri.videocall.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zaidmansuri.videocall.R
import com.zaidmansuri.videocall.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var databaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(mAuth.currentUser!!.uid)
        databaseReference.child("status").setValue("online")
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#2f2e41")))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bnv.setupWithNavController(navController)
    }

    override fun onRestart() {
        databaseReference.child("status").setValue("online")
        super.onRestart()
    }

    override fun onResume() {
        databaseReference.child("status").setValue("online")
        super.onResume()
    }

    override fun onStop() {
        databaseReference.child("status").setValue("offline")
        super.onStop()
    }

    override fun onDestroy() {
        databaseReference.child("status").setValue("offline")
        super.onDestroy()
    }
}