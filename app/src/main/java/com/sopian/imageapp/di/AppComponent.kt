package com.sopian.imageapp.di

import com.sopian.imageapp.core.di.CoreComponent
import com.sopian.imageapp.ui.detail.DetailFragment
import com.sopian.imageapp.ui.detail.MapsBottomSheetFragment
import com.sopian.imageapp.ui.home.HomeFragment
import dagger.Component

@AppScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [AppModule::class, ViewModelModule::class]
)
interface AppComponent {
    @Component.Factory
    interface Factory{
        fun create(coreComponent: CoreComponent): AppComponent
    }

    fun inject(fragment: HomeFragment)
    fun inject(fragment: DetailFragment)
    fun inject(fragment: MapsBottomSheetFragment)
}