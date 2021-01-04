package com.sopian.imageapp.core.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unsplash_info")
data class InfoEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "width")
    val width: Int? = null,

    @ColumnInfo(name = "height")
    val height: Int? = null,

    @Embedded(prefix = "exif_")
    val exif: ExifEntity?,

    @Embedded(prefix = "location_")
    val location: LocationEntity?,

    @ColumnInfo(name = "views")
    val views: Int? = null,

    @ColumnInfo(name = "downloads")
    val downloads: Int? = null
) {

    data class ExifEntity(
        @ColumnInfo(name = "make")
        val make: String? = null,

        @ColumnInfo(name = "model")
        val model: String? = null,

        @ColumnInfo(name = "exposure_time")
        val exposure_time: String? = null,

        @ColumnInfo(name = "aperture")
        val aperture: String? = null,

        @ColumnInfo(name = "focal_length")
        val focal_length: String? = null,

        @ColumnInfo(name = "iso")
        val iso: String? = null
    )

    data class LocationEntity(
        @ColumnInfo(name = "title")
        val title: String? = null,

        @ColumnInfo(name = "name")
        val name: String? = null,

        @ColumnInfo(name = "city")
        val city: String? = null,

        @ColumnInfo(name = "country")
        val country: String? = null,

        @Embedded(prefix = "position_")
        val position: PositionEntity?
    ) {

        data class PositionEntity(
            @ColumnInfo(name = "latitude")
            val latitude: Double? = null,

            @ColumnInfo(name = "longitude")
            val longitude: Double? = null
        )
    }
}