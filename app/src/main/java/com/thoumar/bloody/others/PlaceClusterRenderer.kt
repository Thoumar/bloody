package com.thoumar.bloody.others

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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


class PlaceClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<Place>?) : DefaultClusterRenderer<Place>(context, map, clusterManager) {

    private val height = 80
    private val width = 80

    private val markerFixIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context?.resources,
                com.thoumar.bloody.R.drawable.ic_marker_fix
            ),
            width,
            height,
            false
        )
    )

    private val markerMobileIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
        Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context?.resources,
                com.thoumar.bloody.R.drawable.ic_marker_mobile
            ),
            width,
            height,
            false
        )
    )

    override fun onBeforeClusterItemRendered(place: Place, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(place, markerOptions)

        val str = place.icon

        val icon = when(str) {
            "efs" -> markerFixIcon
            "rou" -> markerMobileIcon
            else ->  markerMobileIcon
        }
        markerOptions
            .icon(icon)
    }
}