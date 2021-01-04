package com.sopian.imageapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("urls")
    val urls: UnsplashPhotoUrls,
    @field:SerializedName("created_at")
    val created_at : String,
    var query: String?,
    @field:SerializedName("user")
    var user: User,
) {

    data class UnsplashPhotoUrls(
        @field:SerializedName("small")
        val small: String,
        @field:SerializedName("regular")
        val regular: String,
    )

    data class User(
        @field:SerializedName("id")
        val id: String,
        @field:SerializedName("username")
        val username: String,
        @field:SerializedName("profile_image")
        val profileImage: ProfileImage
    ){

        data class ProfileImage(
            @field:SerializedName("medium")
            val medium : String
        )
    }

}