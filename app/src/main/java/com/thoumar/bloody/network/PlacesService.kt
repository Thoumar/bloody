package com.thoumar.bloody.network

import com.thoumar.bloody.entities.CollectsResponse
import retrofit2.http.*


interface PlacesService {

    @GET("/get-collects-ajax")
    suspend fun getFromViewBounds(
        @Query("neLon") neLon: Double,
        @Query("neLat") neLat: Double,
        @Query("swLon") swLon: Double,
        @Query("swLat") swLat: Double
    ): CollectsResponse
}