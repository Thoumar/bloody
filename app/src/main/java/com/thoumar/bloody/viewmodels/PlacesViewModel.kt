package com.thoumar.bloody.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.gms.maps.model.LatLngBounds
import com.thoumar.bloody.entities.Place
import com.thoumar.bloody.repositories.PlacesRepository
import kotlinx.coroutines.Dispatchers

class PlacesViewModel : ViewModel() {
    private val repository: PlacesRepository = PlacesRepository()

    fun getPlacesFromBounds(bounds: LatLngBounds): LiveData<List<Place>> = liveData(
        Dispatchers.IO) {
        try {
            val neLon: Double = bounds.northeast.longitude
            val neLat: Double = bounds.northeast.latitude
            val swLon: Double = bounds.southwest.longitude
            val swLat: Double = bounds.southwest.latitude

            val placesList = repository.getFromViewBounds(neLon, neLat, swLon, swLat)
            placesList.forEach {
                Log.d("BLOODY", it.icon)
            }

            emit(placesList)

        } catch (ex: Exception) {
            Log.d("[ERR]", ex.message.toString())
        }
    }
}