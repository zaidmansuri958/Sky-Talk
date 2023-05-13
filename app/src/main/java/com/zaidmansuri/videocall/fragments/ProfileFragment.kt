package com.zaidmansuri.videocall.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zaidmansuri.videocall.R
import com.zaidmansuri.videocall.auth.LoginActivity
import com.zaidmansuri.videocall.databinding.FragmentProfileBinding
import com.zaidmansuri.videocall.model.User

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        requireActivity().title = "Profile"
        binding.progressBar.visibility=View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(mAuth.currentUser!!.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(User::class.java)
                binding.progressBar.visibility=View.GONE
                binding.userEmail.text = data!!.email
                binding.userName.text = "Hello " + data!!.name
                binding.userNumber.text = data!!.number
                Glide.with(requireActivity()).load(data.image).into(binding.userImg)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.logout.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setMessage("Do you rally want to logout ?").setCancelable(false).setPositiveButton(
            "Logout"
        ) { dialogInterface, i ->
            mAuth.signOut()
            Toast.makeText(context, "Logout", Toast.LENGTH_SHORT).show()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }.setNegativeButton(
            "cancel"
        ) { dialogInterface, i -> dialogInterface.cancel() }
        val alertDialog = dialog.create()
        alertDialog.show()
    }


}