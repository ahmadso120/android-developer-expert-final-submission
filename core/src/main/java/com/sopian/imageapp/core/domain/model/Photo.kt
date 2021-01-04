package com.sopian.imageapp.core.domain.model

data class Photo(
    val id: String,
    val description: String?,
    val urls: UnsplashPhotoUrls,
    val isFavorite: Boolean,
    val query: String,
    val created_at : String,
    var user: User,
) {

    data class UnsplashPhotoUrls(
        val small: String,
        val regular: String,
    )

    data class User(
        val id: String,
        val username: String,
        val profileImage: ProfileImage
    ){
        data class ProfileImage(
            val medium : String
        )
    }

}