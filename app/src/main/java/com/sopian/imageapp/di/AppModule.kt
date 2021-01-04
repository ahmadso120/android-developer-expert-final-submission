package com.sopian.imageapp.di

import com.sopian.imageapp.core.domain.usecase.UnsplashInteractor
import com.sopian.imageapp.core.domain.usecase.UnsplashUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class AppModule {

    @Binds
    abstract fun provideUnsplashPhotoUseCase(interactor: UnsplashInteractor): UnsplashUseCase

}