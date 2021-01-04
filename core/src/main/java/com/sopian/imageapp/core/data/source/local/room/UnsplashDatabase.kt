package com.sopian.imageapp.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sopian.imageapp.core.data.source.local.entity.InfoEntity

import com.sopian.imageapp.core.data.source.local.entity.PhotoEntity
import com.sopian.imageapp.core.data.source.local.room.converters.DateConverter

@Database(entities = [PhotoEntity::class, InfoEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class UnsplashDatabase : RoomDatabase() {

    abstract fun photoDao(): UnsplashDao
}