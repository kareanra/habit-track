package com.kareanra.habittrack.di.modules

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule {

    companion object {
        private const val nytBaseUrl = "https://api.nytimes.com"
        private const val oxfordBaseUrl = "https://od-api.oxforddictionaries.com/api/v2"
        private const val nytApiKey = "wLeacrByNfO2Qa0o7wDPCIJlsAKOZChc"
        private const val oxfordApiKey = "fe7ad2ac1da00b5fb436452e36ed2001"
        private const val oxfordAppId = "03de4b13"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val nytAuthInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url()
            .newBuilder()
            .addQueryParameter("api-key", nytApiKey)
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    private val oxfordAuthInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url()
            .newBuilder()
            .addQueryParameter("api-key", oxfordApiKey)
            .addQueryParameter("api-id", oxfordAppId)
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        chain.proceed(newRequest)
    }

    @Provides
    @Singleton
    @Named("NYT")
    fun providesNytHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(nytAuthInterceptor)
            .build()

    @Provides
    @Singleton
    @Named("OXFORD")
    fun providesOxfordHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(oxfordAuthInterceptor)
            .build()

    @Provides
    @Named("NYT")
    fun providesNytRetrofit(@Named("NYT") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(nytBaseUrl)
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()

    @Provides
    @Named("OXFORD")
    fun providesOxfordRetrofit(@Named("OXFORD") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl(oxfordBaseUrl)
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()

//    @Provides
//    @Singleton
//    fun providesNytApi(@Named("NYT") retrofit: Retrofit): NytBooksApi =
//        retrofit.create(NytBooksApi::class.java)
//
//    @Provides
//    @Singleton
//    fun providesOxfordApi(@Named("OXFORD") retrofit: Retrofit): OxfordDictionaryApi =
//        retrofit.create(OxfordDictionaryApi::class.java)
}