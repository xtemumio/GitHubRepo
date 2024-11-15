package com.example.githubrepo

import com.google.gson.annotations.SerializedName

data class User(
    val name: String,
    val bio: String?,
    val company: String?,
    val location: String?,
    @SerializedName("public_repos") val publicRepos: Int,
    @SerializedName("avatar_url") val avatarUrl: String
)