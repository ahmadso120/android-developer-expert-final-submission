package com.sopian.imageapp.favorite.di

import com.sopian.imageapp.core.domain.usecase.UnsplashInteractor
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class FeatureModule {

    @Binds
    abstract fun provideUnsplashPhotoUseCase(interactor: UnsplashInteractor): UnsplashUseCase
}