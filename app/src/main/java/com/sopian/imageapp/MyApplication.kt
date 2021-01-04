package com.sopian.imageapp

import android.app.Application
import com.sopian.imageapp.core.di.CoreComponent
import com.sopian.imageapp.core.di.DaggerCoreComponent
import com.sopian.imageapp.core.utils.ReleaseTree
import com.sopian.imageapp.di.AppComponent
import com.sopian.imageapp.di.DaggerAppComponent
import timber.log.Timber

open class MyApplication : Application(){

    val coreComponent: CoreComponent by lazy {
        DaggerCoreComponent.factory().create(applicationContext)
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(coreComponent)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }else{
            Timber.plant(ReleaseTree())
        }
    }
}