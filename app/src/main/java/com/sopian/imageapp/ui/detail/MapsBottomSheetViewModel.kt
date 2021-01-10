package com.sopian.imageapp.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import timber.log.Timber
import javax.inject.Inject

class MapsBottomSheetViewModel @Inject constructor(
    private val useCase: UnsplashUseCase
) : ViewModel(){

    private val _setIdDetail = MutableLiveData<String>()

    val infoData = _setIdDetail.switchMap {
        useCase.getInfo(it).asLiveData()
    }

    fun setIdDetail(id: String){
        Timber.tag("setIdDetail").d(id)
        _setIdDetail.value = id
    }
}