package com.sopian.imageapp.favorite

import androidx.lifecycle.*
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import com.sopian.imageapp.core.utils.Event
import com.sopian.imageapp.favorite.di.FeatureScope
import javax.inject.Inject

@FeatureScope
class FavoriteViewModel @Inject constructor(
    useCase: UnsplashUseCase
) : ViewModel() {
    val favorites =
        useCase.getFavoritePhoto().asLiveData()

    private val _navigateToDetail = MutableLiveData<Event<Photo>>()
    val navigateToDetail: LiveData<Event<Photo>>
        get() = _navigateToDetail

    fun onPhotoClicked(photo: Photo) {
        _navigateToDetail.value = Event(photo)
    }
}