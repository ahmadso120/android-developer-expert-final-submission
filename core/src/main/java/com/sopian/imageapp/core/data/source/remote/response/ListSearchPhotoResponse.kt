package com.sopian.imageapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class ListSearchPhotoResponse(
    @field:SerializedName("results")
    val results: List<PhotoResponse>
)
