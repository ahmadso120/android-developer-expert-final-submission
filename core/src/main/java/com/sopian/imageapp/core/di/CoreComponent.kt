package com.sopian.imageapp.core.di

import android.content.Context
import com.sopian.imageapp.core.domain.repository.IUnsplashRepository
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RepositoryModule::class]
)
interface CoreComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CoreComponent
    }

    fun provideUnsplashPhotoRepository() : IUnsplashRepository
}