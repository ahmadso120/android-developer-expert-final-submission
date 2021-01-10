package com.sopian.imageapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sopian.imageapp.ui.ViewModelFactory
import com.sopian.imageapp.ui.detail.DetailViewModel
import com.sopian.imageapp.ui.detail.MapsBottomSheetViewModel
import com.sopian.imageapp.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    abstract fun bindDetailViewModel(viewModel: DetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapsBottomSheetViewModel::class)
    abstract fun bindMapsBottomSheetViewModel(viewModel: MapsBottomSheetViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}