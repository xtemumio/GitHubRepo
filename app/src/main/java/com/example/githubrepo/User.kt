package com.example.githubrepo

import com.google.gson.annotations.SerializedName

data class User(
    val login: String?, // Aggiunto il campo login
    val name: String?,
    val bio: String?,
    val company: String?,
    val location: String?,
    val email : String?,
    @SerializedName("public_repos") val publicRepos: Int,
    @SerializedName("avatar_url") val avatarUrl: String,
    val followers: Int?,
    val following: Int?,
    val contributions: IntArray?
)