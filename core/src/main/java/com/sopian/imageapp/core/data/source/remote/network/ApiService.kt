package com.sopian.imageapp.core.data.source.remote.network

import com.sopian.imageapp.core.BuildConfig
import com.sopian.imageapp.core.data.source.remote.response.*
import retrofit2.http.*

interface ApiService {

    companion object {
        const val CLIENT_ID = BuildConfig.API_KEY
    }

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos?page=1&per_page=40")
    suspend fun getPhotosByQuery(
        @Query("query") query: String
    ) : ListSearchPhotoResponse

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("photos/{id}/download")
    suspend fun download(
        @Path("id") id: String
    ) : DownloadResponse

    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("photos/{id}")
    suspend fun getInfo(
        @Path("id") id : String
    ) : InfoResponse
}
