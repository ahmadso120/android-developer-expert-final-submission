package com.sopian.imageapp.core.data

import com.sopian.imageapp.core.data.source.local.LocalDataSource
import com.sopian.imageapp.core.data.source.remote.RemoteDataSource
import com.sopian.imageapp.core.data.source.remote.network.ApiResponse
import com.sopian.imageapp.core.data.source.remote.response.DownloadResponse
import com.sopian.imageapp.core.data.source.remote.response.InfoResponse
import com.sopian.imageapp.core.data.source.remote.response.PhotoResponse
import com.sopian.imageapp.core.domain.model.Download
import com.sopian.imageapp.core.domain.model.Info
import com.sopian.imageapp.core.domain.model.Photo
import com.sopian.imageapp.core.domain.repository.IUnsplashRepository
import com.sopian.imageapp.core.utils.AppExecutors
import com.sopian.imageapp.core.utils.CheckConnection
import com.sopian.imageapp.core.utils.DataMapper
import com.sopian.imageapp.core.utils.RateLimiter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors,
    private val checkConnection: CheckConnection
) : IUnsplashRepository{

    companion object {
        private const val LIST_SEARCH_PHOTO = "LIST_SEARCH_PHOTO"
    }

    private val rateLimiter = RateLimiter<String>(5, TimeUnit.MINUTES)

    override fun getPhoto(id: String): Flow<Photo> {
        Timber.d(id)
        return localDataSource.getPhoto(id).map {
            DataMapper.mapEntityToDomain(it)
        }
    }

    override fun getPhotosByQuery(query: String): Flow<Resource<out List<Photo>>> {
        Timber.d(query)
        return object : NetworkBoundResource<List<Photo>, List<PhotoResponse>>() {
            override fun loadFromDB(): Flow<List<Photo>> =
                localDataSource.getSearchPhotos(query).map {
                    DataMapper.mapEntitiesToDomain(it)
                }

            override fun shouldFetch(data: List<Photo>?): Boolean{
                if(!checkConnection.isConnected) return false

                return data == null || data.isEmpty() ||
                        rateLimiter.shouldFetch(LIST_SEARCH_PHOTO)
            }

            override suspend fun createCall(): Flow<ApiResponse<List<PhotoResponse>>> =
                remoteDataSource.getPhotosByQuery(query)

            override suspend fun saveCallResult(data: List<PhotoResponse>) {
                data.map {
                    it.query = query
                }
                localDataSource.insertPhoto(DataMapper.mapResponsesToEntities(data))
            }

            override fun onFetchFailed() {
                rateLimiter.reset(LIST_SEARCH_PHOTO)
            }
        }.asFlow()
    }

    override fun getFavoritePhoto(): Flow<List<Photo>> {
        return localDataSource.getFavoritePhoto().map {
            DataMapper.mapEntitiesToDomain(it)
        }
    }

    override fun setFavoritePhoto(photo: Photo, state: Boolean) {
        val photoEntity = DataMapper.mapDomainToEntity(photo)
        appExecutors.diskIO().execute {
            localDataSource.setFavoritePhoto(photoEntity, state)
        }
    }

    override fun getDownloadUrl(id: String): Flow<Resource<out Download>> =
        object: NetworkService<Download, DownloadResponse>(){
            override suspend fun createCall(): Flow<ApiResponse<DownloadResponse>> {
                return remoteDataSource.getDownloadUrl(id)
            }

            override fun load(data: DownloadResponse?): Flow<Download> {
                return flow {
                    emit(DataMapper.mapDownloadResponseToDomain(data))
                }
            }

        }.asFlow()

    override fun getInfo(id: String): Flow<Resource<out Info>> =
        object : NetworkBoundResource<Info, InfoResponse>() {
            override fun loadFromDB(): Flow<Info> =
                localDataSource.getInfo(id).map {
                    DataMapper.mapInfoEntityToDomain(it)
                }

            override fun shouldFetch(data: Info?): Boolean {
                if(!checkConnection.isConnected) return false
                return data?.id == null || rateLimiter.shouldFetch(id)
            }

            override suspend fun createCall(): Flow<ApiResponse<InfoResponse>> =
                remoteDataSource.getInfo(id)

            override suspend fun saveCallResult(data: InfoResponse) {
                localDataSource.insertInfo(DataMapper.mapInfoResponseToEntity(data))
            }

            override fun onFetchFailed() {
                rateLimiter.reset(id)
            }
        }.asFlow()
}

