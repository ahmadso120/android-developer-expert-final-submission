package com.sopian.imageapp.core.data.source.local

import com.sopian.imageapp.core.data.source.local.entity.InfoEntity
import com.sopian.imageapp.core.data.source.local.entity.PhotoEntity
import com.sopian.imageapp.core.data.source.local.room.UnsplashDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val dao: UnsplashDao
){
    fun getSearchPhotos(query: String? = null): Flow<List<PhotoEntity>> = dao.getSearchPhotos(query)

    fun getPhoto(id: String) : Flow<PhotoEntity> = dao.getPhoto(id)

    fun getFavoritePhoto(): Flow<List<PhotoEntity>> = dao.getFavoritePhoto()

    suspend fun insertPhoto(list: List<PhotoEntity>) =
        dao.insertPhotos(list)

    fun setFavoritePhoto(photo: PhotoEntity, newState: Boolean) {
        photo.isFavorite = newState
        dao.updateFavoritePhoto(photo)
    }

    suspend fun insertInfo(data: InfoEntity) = dao.insertInfo(data)

    fun getInfo(id: String) : Flow<InfoEntity> = dao.getInfo(id)
}