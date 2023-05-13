package com.zaidmansuri.videocall.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zaidmansuri.videocall.R
import com.zaidmansuri.videocall.activities.VideoActivity
import com.zaidmansuri.videocall.adapter.CallAdapter
import com.zaidmansuri.videocall.databinding.FragmentCallBinding
import com.zaidmansuri.videocall.model.User
import java.util.*

class CallFragment : Fragment() {
    private lateinit var binding: FragmentCallBinding
    private lateinit var userAdapter: CallAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var callReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_call, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCallBinding.bind(view)
        mAuth = FirebaseAuth.getInstance()
        requireActivity().title = "Users"
        binding.progressBar.visibility=View.VISIBLE
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        callReference = FirebaseDatabase.getInstance().getReference("videoCall")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = arrayListOf<User>()
                for (i in snapshot.children) {
                    if (i.key.equals(mAuth.currentUser!!.uid)) {
                        userNumber = i.getValue(User::class.java)!!.number.toString()
                        continue
                    }
                    val user = i.getValue(User::class.java)
                    userList.add(user!!)
                }
                userAdapter = CallAdapter(userList)
                binding.userRecycler.apply {
                    adapter = userAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
                binding.progressBar.visibility=View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        callReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(userNumber)) {
                    binding.callLayout.visibility = View.VISIBLE
                    onCallRequest()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun onCallRequest() {
        val uniqueId = getUniId()
        callReference.child(userNumber).child("incoming")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.callerName.text=snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        binding.callLayout.visibility = View.VISIBLE
        binding.btnAccept.setOnClickListener {
            callReference.child(userNumber).child("connId").setValue(uniqueId)
            callReference.child(userNumber).child("isAvailable").setValue(true)
            binding.callLayout.visibility = View.GONE
            startActivity(Intent(activity, VideoActivity::class.java))
        }
        binding.btnReject.setOnClickListener {
            callReference.child(userNumber).child("incoming").setValue(null)
            binding.callLayout.visibility = View.GONE
        }

    }

    private fun getUniId(): String {
        return UUID.randomUUID().toString()
    }
}