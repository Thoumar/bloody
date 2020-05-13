package com.thoumar.bloody.others

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.thoumar.bloody.entities.Place


class PlaceClusterRenderer(val context: Context?, val map: GoogleMap?, clusterManager: ClusterManager<Place>?) : DefaultClusterRenderer<Place>(context, map, clusterManager) {

    private val height = 100
    private val width = 100
    val b: Bitmap = BitmapFactory.decodeResource(context?.resources, com.thoumar.bloody.R.drawable.place_icon)
    private val smallMarker: Bitmap = Bitmap.createScaledBitmap(b, width, height, false)
    private val smallMarkerIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallMarker)

    override fun onBeforeClusterItemRendered(item: Place, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)

        markerOptions
            .title("")
            .icon(smallMarkerIcon)
    }
}