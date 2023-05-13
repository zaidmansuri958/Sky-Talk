package com.zaidmansuri.videocall.model

data class User(
    val name: String? = "",
    val email: String? = "",
    val number:String?="",
    val password:String?="",
    val status: String? = "",
    val image: String? = ""
)