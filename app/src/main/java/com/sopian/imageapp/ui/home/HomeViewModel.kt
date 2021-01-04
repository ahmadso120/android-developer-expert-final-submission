package com.sopian.imageapp.ui.home

import androidx.lifecycle.*
import com.sopian.imageapp.core.data.Resource
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import com.sopian.imageapp.core.utils.Event
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val useCase: UnsplashUseCase
) : ViewModel() {

    private val _photosLiveData = useCase.getPhotosByQuery("people").asLiveData()
    val photosMediatorData = MediatorLiveData<Resource<out List<Photo>>>()
    private var _searchMoviesLiveData: LiveData<Resource<out List<Photo>>>
    private val _searchFieldTextLiveData = MutableLiveData<String>()

    init {
        _searchMoviesLiveData = _searchFieldTextLiveData.switchMap {
            useCase.getPhotosByQuery(it).asLiveData()
        }

        photosMediatorData.addSource(_photosLiveData){
            photosMediatorData.value = it
        }

        photosMediatorData.addSource(_searchMoviesLiveData){
            photosMediatorData.value = it
        }
    }

    private val _navigateToDetail = MutableLiveData<Event<Photo>>()

    val navigateToDetail: LiveData<Event<Photo>>
        get() = _navigateToDetail

    fun onPhotoClicked(photo: Photo) {
        _navigateToDetail.value = Event(photo)
    }

    fun onSearchQuery(query: String) {
        Timber.d(query)
        _searchFieldTextLiveData.value = query
    }
}