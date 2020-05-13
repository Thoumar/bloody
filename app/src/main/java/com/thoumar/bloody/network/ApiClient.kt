package com.thoumar.bloody.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiClient {
    companion object {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        private var client: Retrofit = Retrofit.Builder()
            .baseUrl("https://dondesang.efs.sante.fr")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        var placesService: PlacesService = client.create(PlacesService::class.java)
    }
}