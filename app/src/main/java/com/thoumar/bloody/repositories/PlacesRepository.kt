package com.thoumar.bloody.repositories

import com.thoumar.bloody.entities.Place
import com.thoumar.bloody.network.ApiClient
import com.thoumar.bloody.network.PlacesService

class PlacesRepository {

    private val placesClient: PlacesService = ApiClient.placesService

    suspend fun getFromViewBounds(neLon: Double, neLat: Double, swLon: Double, swLat: Double): List<Place> {
        val response = placesClient.getFromViewBounds(neLon, neLat, swLon, swLat)
        return response.results
    }
}