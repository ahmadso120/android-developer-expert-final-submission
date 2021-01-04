package com.sopian.imageapp.favorite.di

import com.sopian.imageapp.core.di.CoreComponent
import com.sopian.imageapp.favorite.FavoriteFragment
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class]
    , modules = [
        FeatureModule::class,
        ViewModelModule::class
    ]
)
interface DynamicFeatureComponent {

    @Component.Factory
    interface Factory{
        fun create(coreComponent: CoreComponent): DynamicFeatureComponent
    }

    fun inject(fragment: FavoriteFragment)
}