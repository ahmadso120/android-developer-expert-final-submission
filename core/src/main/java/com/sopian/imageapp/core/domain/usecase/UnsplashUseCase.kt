package com.sopian.imageapp.core.domain.usecase

import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.domain.model.Info
import com.sopian.imageapp.core.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface UnsplashUseCase {
    fun getPhoto(id: String) : Flow<Photo>
    fun getPhotosByQuery(query: String): Flow<Resource<out List<Photo>>>
    fun getFavoritePhoto(): Flow<List<Photo>>
    fun setFavoritePhoto(photo: Photo, state: Boolean)
    fun getDownloadUrl(id: String): Flow<Resource<out Download>>
    fun getInfo(id: String) : Flow<Resource<out Info>>
}