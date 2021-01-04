package com.sopian.imageapp.core.data

import com.sopian.imageapp.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

abstract class NetworkService<ResultType, RequestType> {

    private var result: Flow<Resource<out ResultType>> = flow {
        emit(Resource.Loading())
        when (val apiResponse = createCall().first()) {
            is ApiResponse.Success -> {
                emitAll(load(apiResponse.data).map { Resource.Success(it) })
            }
            is ApiResponse.Empty -> {
                emitAll(load().map { Resource.Success(it) })
            }
            is ApiResponse.Error -> {
                onFetchFailed()
                emit(Resource.Error(apiResponse.errorMessage, null))
            }
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun load(data: RequestType? = null): Flow<ResultType>

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    fun asFlow(): Flow<Resource<out ResultType>> = result
}