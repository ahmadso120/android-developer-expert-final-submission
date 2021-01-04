package com.sopian.imageapp.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class DownloadResponse (
    @field:SerializedName("url")
    val url: String
)