package com.sopian.imageapp.core.di

import com.sopian.imageapp.core.data.UnsplashRepository
import com.sopian.imageapp.core.domain.repository.IUnsplashRepository
import dagger.Binds
import dagger.Module

@Module(includes = [NetworkModule::class, DatabaseModule::class])
abstract class RepositoryModule {

    @Binds
    abstract fun provideUnsplashPhotoRepository(repository: UnsplashRepository): IUnsplashRepository

}