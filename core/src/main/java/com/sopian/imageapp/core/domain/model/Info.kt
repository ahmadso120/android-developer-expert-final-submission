package com.sopian.imageapp.core.domain.model

data class Info(
    val id: String,
    val createdAt: String,
    val width: Int? = null,
    val height: Int? = null,
    val exif: Exif?,
    val location: Location?,
    val views: Int? = null,
    val downloads: Int? = null
) {

    data class Exif(
        val make: String? = null,
        val model: String? = null,
        val exposure_time: String? = null,
        val aperture: String? = null,
        val focal_length: String? = null,
        val iso: String? = null
    )

    data class Location(
        val title: String? = null,
        val name: String? = null,
        val city: String? = null,
        val country: String? = null,
        val position: Position?
    ) {

        data class Position(
            val latitude: Double? = null,
            val longitude: Double? = null
        )
    }

    val dimensions: String
        get(){
            return if (width == null && height == null){
                "--"
            }else{
                "$width x $height"
            }
        }
}