package com.sopian.imageapp.core.data.source.local.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "unsplash_photo",
    indices = [
        Index("id"),
        Index("user_id")
    ],
    primaryKeys = ["id", "user_id"]
)
data class PhotoEntity(
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "description")
    val description: String?,
    @Embedded(prefix = "urls_")
    var urls: UnsplashPhotoUrls,
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Boolean = false,
    @ColumnInfo(name = "dateCreated")
    val created: Date = Date(),
    @ColumnInfo(name = "created_at")
    val created_at : String,
    @ColumnInfo(name = "query")
    val query: String?,
    @Embedded(prefix = "user_")
    var user: User,

){
    data class UnsplashPhotoUrls(
        @ColumnInfo(name = "small")
        val small: String,
        @ColumnInfo(name = "regular")
        val regular: String,
    )

    data class User(
        @ColumnInfo(name = "id")
        val id: String,
        @ColumnInfo(name = "username")
        val username: String,
        @Embedded(prefix = "profileImage_")
        val profileImage: ProfileImage
    ){
        data class ProfileImage(
            @ColumnInfo(name = "medium")
            val medium : String
        )
    }
}