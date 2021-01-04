package com.sopian.imageapp.core.data.source.remote

import android.annotation.SuppressLint
import com.sopian.imageapp.core.data.source.remote.network.ApiResponse
import com.sopian.imageapp.core.data.source.remote.network.ApiService
import com.sopian.imageapp.core.data.source.remote.response.DownloadResponse
import com.sopian.imageapp.core.data.source.remote.response.InfoResponse
import com.sopian.imageapp.core.data.source.remote.response.PhotoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
){

    suspend fun getPhotosByQuery(query: String): Flow<ApiResponse<List<PhotoResponse>>> {
        return flow {
            try {
                val response = apiService.getPhotosByQuery(query)
                Timber.d(response.toString())
                val dataArray = response.results
                if (dataArray.isNotEmpty()) {
                    emit(ApiResponse.Success(response.results))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.e(e.toString() )
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getInfo(id: String): Flow<ApiResponse<InfoResponse>> {
        return flow {
            try {
                val response = apiService.getInfo(id)
                Timber.d(response.toString())
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    @SuppressLint("CheckResult")
    fun getDownloadUrl(id: String) : Flow<ApiResponse<DownloadResponse>> {
        return flow {
            try {
                val response = apiService.download(id)
                Timber.d(response.toString())
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}