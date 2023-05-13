package com.zaidmansuri.videocall.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.zaidmansuri.videocall.activities.VideoActivity
import com.zaidmansuri.videocall.databinding.UserCardBinding
import com.zaidmansuri.videocall.model.User

class CallAdapter(private val users: ArrayList<User>) : RecyclerView.Adapter<CallViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val view = UserCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val data = users.get(position)
        holder.binding.userName.text = data.name
        holder.binding.userNumber.text = data.number
        holder.binding.userStatus.text = data.status
        if(data.status=="offline"){
            holder.binding.btnCall.visibility= View.GONE
            holder.binding.userStatus.setTextColor(Color.parseColor("#E30022"))
        }
        Glide.with(holder.itemView).load(data.image).into(holder.binding.userImg)
        holder.binding.btnCall.setOnClickListener {
            val intent = Intent(holder.itemView.context, VideoActivity::class.java)
            intent.putExtra("friendId", data.number)
            holder.itemView.context.startActivity(intent)
        }
    }
}

class CallViewHolder(val binding: UserCardBinding) : ViewHolder(binding.root)