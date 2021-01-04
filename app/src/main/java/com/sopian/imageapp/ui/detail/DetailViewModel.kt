package com.sopian.imageapp.ui.detail

import androidx.lifecycle.*
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DetailViewModel @Inject constructor(
    private val useCase: UnsplashUseCase
) : ViewModel() {

    private val _setIdDetail = MutableLiveData<String>()
    private val _download = MutableLiveData<String>()

    val photoData = _setIdDetail.switchMap {
        useCase.getPhoto(it).asLiveData()
    }

    val infoData = _setIdDetail.switchMap {
        useCase.getInfo(it).asLiveData()
    }

    fun setIdDetail(id: String){
        Timber.tag("setIdDetail").d(id)
        _setIdDetail.value = id
    }

    fun onDownloadClicked(id: String){
        Timber.tag("onDownloadClicked").d(id)
        _download.value = id
    }

    val download = _download.switchMap {
        useCase.getDownloadUrl(it).asLiveData()
    }

    fun setFavoritePhoto(photo: Photo, newStatus:Boolean) {
        viewModelScope.launch {
            useCase.setFavoritePhoto(photo, newStatus)
        }
    }
}

