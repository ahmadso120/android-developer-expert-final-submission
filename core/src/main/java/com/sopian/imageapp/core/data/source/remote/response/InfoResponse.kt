package com.sopian.imageapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class InfoResponse (
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("height")
    val height: Int? = null,

    @field:SerializedName("exif")
    val exif: ExifResponse?,

    @field:SerializedName("location")
    val location: LocationResponse?,

    @field:SerializedName("views")
    val views: Int? = null,

    @field:SerializedName("downloads")
    val downloads: Int? = null
){

    data class ExifResponse(
        @field:SerializedName("make")
        val make: String? = null,

        @field:SerializedName("model")
        val model: String? = null,

        @field:SerializedName("exposure_time")
        val exposure_time: String? = null,

        @field:SerializedName("aperture")
        val aperture: String? = null,

        @field:SerializedName("focal_length")
        val focal_length: String? = null,

        @field:SerializedName("iso")
        val iso: String? = null
    )

    data class LocationResponse(
        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("city")
        val city: String? = null,

        @field:SerializedName("country")
        val country: String? = null,

        @field:SerializedName("position")
        val position: PositionResponse?
    ){

        data class PositionResponse(
            @field:SerializedName("latitude")
            val latitude: Double? = null,

            @field:SerializedName("longitude")
            val longitude: Double? = null
        )
    }
}