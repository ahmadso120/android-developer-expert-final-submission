package com.sopian.imageapp.core.domain.usecase

import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.domain.model.Info
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.domain.repository.IUnsplashRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnsplashInteractor @Inject constructor(
    private val unsplashRepository: IUnsplashRepository
): UnsplashUseCase {

    override fun getPhoto(id: String): Flow<Photo> =
        unsplashRepository.getPhoto(id)

    override fun getPhotosByQuery(query: String): Flow<Resource<out List<Photo>>> =
        unsplashRepository.getPhotosByQuery(query)

    override fun getFavoritePhoto(): Flow<List<Photo>> =
        unsplashRepository.getFavoritePhoto()

    override fun setFavoritePhoto(photo: Photo, state: Boolean) =
        unsplashRepository.setFavoritePhoto(photo, state)

    override fun getDownloadUrl(id: String): Flow<Resource<out Download>> =
        unsplashRepository.getDownloadUrl(id)

    override fun getInfo(id: String): Flow<Resource<out Info>>{
        return unsplashRepository.getInfo(id)
    }

}

