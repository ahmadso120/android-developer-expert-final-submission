package com.sopian.imageapp.core.di

import com.sopian.imageapp.core.BuildConfig
import com.sopian.imageapp.core.data.source.remote.network.ApiService
import dagger.Module
import dagger.Provides
import okhttp3.CertificatePinner
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val hostname = "api.unsplash.com"
        val certificatePinner = CertificatePinner.Builder()
            .add(hostname, "sha256/60J+uBsULLchqgoeQGCJeLilfJP/JWzhwUb06mXkvGM=")
            .add(hostname, "sha256/+VZJxHgrOOiVyUxgMRbfoo+GIWrMKd4aellBBHtBcKg=")
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }

    @Provides
    fun provideApiService(
        okHttpClient: OkHttpClient
    ): ApiService {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
        return retrofitBuilder.build().create(ApiService::class.java)
    }
}
