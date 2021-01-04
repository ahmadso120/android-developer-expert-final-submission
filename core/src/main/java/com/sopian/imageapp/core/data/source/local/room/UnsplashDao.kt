package com.sopian.imageapp.core.data.source.local.room

import androidx.room.*
import com.sopian.imageapp.core.data.source.local.entity.InfoEntity
import com.sopian.imageapp.core.data.source.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnsplashDao {

    @Query("SELECT * FROM unsplash_photo WHERE `query` = :query ORDER BY dateCreated DESC LIMIT 40")
    fun getSearchPhotos(query: String? = null): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM unsplash_photo WHERE id = :id")
    fun getPhoto(id: String): Flow<PhotoEntity>

    @Query("SELECT * FROM unsplash_photo where isFavorite = 1")
    fun getFavoritePhoto(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

    @Update
    fun updateFavoritePhoto(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInfo(info: InfoEntity)

    @Query("SELECT * FROM unsplash_info WHERE id = :id")
    fun getInfo(id: String): Flow<InfoEntity>
}