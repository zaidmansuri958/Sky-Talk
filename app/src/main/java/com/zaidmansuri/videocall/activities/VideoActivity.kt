package com.zaidmansuri.videocall.activities

import android.content.pm.PackageManager
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zaidmansuri.videocall.interfaces.JavaScriptInterface
import com.zaidmansuri.videocall.R
import com.zaidmansuri.videocall.databinding.ActivityVideoBinding
import com.zaidmansuri.videocall.model.User
import java.util.UUID


class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var dbReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    var userId = ""
    var uid = ""
    var friendsUserId = ""
    var isPeerConnected = false
    var firebaseRef = Firebase.database.getReference("videoCall")

    var isAudio = true
    var isVideo = true
    var isStarted = false
    val permissions =
        arrayOf<String>(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    private val requestCode: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
        dbReference =
            FirebaseDatabase.getInstance().getReference("users").child(mAuth.currentUser!!.uid)
        dbReference.child("status").setValue("online")

        databaseReference = FirebaseDatabase.getInstance().getReference()
        friendsUserId = intent.getStringExtra("friendId").toString()
        uid = mAuth.currentUser!!.uid.toString()

        //ask for the permissions
        if (!isGranted()) {
            askPermission()
        }

        //fetch the current user number
        databaseReference.child("users").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(User::class.java)
                    userId = data!!.number.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })


        binding.dial.setOnClickListener {
            if (isStarted == true) {
                finish()
            } else {
                isStarted = true
                binding.progressBar.visibility=View.VISIBLE
                sendCallRequest()
                binding.dial.text = "End call"
                binding.dial.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.offline)));
            }

        }

        binding.audio.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            if (isAudio == false) {
                binding.audio.setImageResource(R.drawable.mute_audio)
            } else {
                binding.audio.setImageResource(R.drawable.normal_audio)
            }
        }

        binding.video.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            if (isVideo == false) {
                binding.video.setImageResource(R.drawable.mute_video)
            } else {
                binding.video.setImageResource(R.drawable.normal_video)
            }
        }

        setupWebView()
    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            binding.progressBar.visibility=View.GONE
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG)
                .show()
            return
        }
        firebaseRef.child(friendsUserId).child("incoming").setValue(userId)
        firebaseRef.child(friendsUserId).child("isAvailable")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value.toString() == "true") {
                        listenForConnId()
                    }
                }
            })
    }

    private fun listenForConnId() {
        firebaseRef.child(friendsUserId).child("connId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null)
                        return
                    callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
                }
            })
    }

    private fun isGranted(): Boolean {
        for (i in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    this@VideoActivity,
                    i
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(this@VideoActivity, permissions, requestCode)
    }

    private fun setupWebView() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.mediaPlaybackRequiresUserGesture = false
        binding.webView.addJavascriptInterface(JavaScriptInterface(this), "Android")
        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        binding.webView.loadUrl(filePath)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {
        uniqueId = getUniId()
        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(userId).child("incoming")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    onCallRequest(snapshot.value as? String)
                }
            })
    }

    private fun onCallRequest(caller: String?) {
        if (caller == null)
            return

        binding.callLayout.visibility = View.VISIBLE
        binding.callerName.text = "$caller is calling..."
        binding.btnAccept.setOnClickListener {
            firebaseRef.child(userId).child("connId").setValue(uniqueId)
            firebaseRef.child(userId).child("isAvailable").setValue(true)
            binding.dial.text = "End call"
            binding.dial.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.offline)));
            isStarted = true
            binding.callLayout.visibility = View.GONE
            binding.progressBar.visibility=View.GONE
        }
        binding.btnReject.setOnClickListener {
            firebaseRef.child(userId).child("incoming").setValue(null)
            binding.callLayout.visibility = View.GONE
            binding.progressBar.visibility=View.GONE
        }

    }

    private fun callJavascriptFunction(functionString: String) {
        binding.webView.post { binding.webView.evaluateJavascript(functionString, null) }
    }

    private fun getUniId(): String {
        return UUID.randomUUID().toString()
    }

    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        firebaseRef.child(friendsUserId).setValue(null)
        binding.webView.loadUrl("about:blank")
        super.onDestroy()
    }
}