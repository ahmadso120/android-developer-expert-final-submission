package com.sopian.imageapp.core.di

import android.content.Context
import androidx.room.Room
import com.sopian.imageapp.core.data.source.local.room.UnsplashDao
import com.sopian.imageapp.core.data.source.local.room.UnsplashDatabase
import dagger.Module
import dagger.Provides
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): UnsplashDatabase {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("unsplash".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            UnsplashDatabase::class.java, "Unsplash.db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    fun provideUnsplashPhotoDao(database: UnsplashDatabase): UnsplashDao = database.photoDao()
}
