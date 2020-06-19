package com.thoumar.bloody.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.maps.android.clustering.ClusterManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.thoumar.bloody.R
import com.thoumar.bloody.activities.PlaceActivity
import com.thoumar.bloody.adapters.PlaceAdapter
import com.thoumar.bloody.entities.Place
import com.thoumar.bloody.others.OnSnapPositionChangeListener
import com.thoumar.bloody.others.PlaceClusterRenderer
import com.thoumar.bloody.others.SnapOnScrollListener
import com.thoumar.bloody.viewmodels.PlacesViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import java.io.IOException
import kotlin.math.*

class MapFragment : Fragment(), OnMapReadyCallback, PermissionListener {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private lateinit var placesViewModel: PlacesViewModel

    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private var places = emptyList<Place>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var clusterManager: ClusterManager<Place>? = null
    private var lastLocation: Location? = null
    private val initialBounds = LatLngBounds(
        LatLng(-85.0, -70.0), // SouthWest
        LatLng(70.0, 85.0) // North East
    )

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_map, container, false)

        // Animation
        v.progressBar.visibility = View.VISIBLE

        // View Model
        placesViewModel = ViewModelProvider(this).get(PlacesViewModel::class.java)

        // Map
        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fusedLocationProviderClient = FusedLocationProviderClient(this.requireActivity())
        map = v.findViewById(R.id.map) as MapView
        map.onCreate(savedInstanceState)
        map.onResume()
        map.getMapAsync(this)

        return v
    }

    @Throws(InterruptedException::class, IOException::class)
    open fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onMapReady(mMap: GoogleMap) {

        // Map initialization
        googleMap = mMap

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.activity,
            R.raw.map_style_light
        ))

        googleMap.setPadding(0, 0, 0, (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175f, resources.displayMetrics)).toInt())

        // ClusterSetup
        clusterManager = ClusterManager<Place>(activity?.applicationContext, googleMap)
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)
        clusterManager?.setOnClusterItemClickListener { onMapPlaceClicked(it) }

        // Is connected
        if (isOnline()) {

            // Get places
            placesViewModel.getPlacesFromBounds(initialBounds).observe(this, Observer { placesList: List<Place> ->
                if(lastLocation !== null) {
                    Log.d("[APP]", "Last location is known, adding distance property")
                    placesList.map { place ->
                        place.range = getDistanceFromLatLonInKm(LatLng(lastLocation!!.latitude, lastLocation!!.longitude), place.position)
                    }
                    places = placesList.sortedBy { place ->
                        place.range
                    }
                } else {
                    Log.d("[APP]", "Last location is unknown, no sorting is made")
                    places = placesList
                }

                placesRcView.layoutManager = LinearLayoutManager(this@MapFragment.context, LinearLayoutManager.HORIZONTAL, false)
                placesRcView.adapter = PlaceAdapter(places) { place -> onRcPlaceClicked(place) }
                places.forEach { place -> clusterManager?.addItem(place) }
                clusterManager?.cluster()
                setSnapHelperBehaviorForRecyclerView()
                progressBar.visibility = View.GONE
            })
        } else {
            val builder: CFAlertDialog.Builder = CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setIcon(R.drawable.ic_no_internet)
                .setTitle("Internet indisponible")
                .setMessage("Vérifiez votre connection à internet et réessayez")
                .addButton("OK", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.CENTER) { dialog, _ ->
                    dialog.dismiss()
                    activity?.finish()
                }
            builder.show()
        }

        clusterManager?.renderer = PlaceClusterRenderer(activity, googleMap, clusterManager)
        checkPermission()
    }

    private fun getDistanceFromLatLonInKm(latLngOne: LatLng, latLngTwo: LatLng): Double {
        val lat1 = latLngOne.latitude
        val lon1 = latLngOne.longitude
        val lat2 = latLngTwo.latitude
        val lon2 = latLngTwo.longitude

        val r = 6371
        val dLat = deg2rad(lat2-lat1)
        val dLon = deg2rad(lon2-lon1)
        val a = sin(dLat/2) * sin(dLat/2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * sin(dLon/2) * sin(dLon/2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return round(r * c * 10.0) / 10.0
    }

    private fun deg2rad(deg: Double) : Double {
        return deg * (Math.PI/180)
    }

    private fun onMapPlaceClicked(place: Place): Boolean {
        val indexOfSelectedPlace = places.lastIndexOf(place)
        if (indexOfSelectedPlace != -1) {
            placesRcView.smoothSnapToPosition(indexOfSelectedPlace)
        }
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(place.latitude, place.longitude)))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun onRcPlaceClicked(place: Place) {
        val intent = Intent(context, PlaceActivity::class.java)
        intent.putExtra("PLACE", place)
        startActivity(intent)
    }

    private fun checkPermission() {
        if (isPermissionGiven()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
    }

    private fun setSnapHelperBehaviorForRecyclerView() {
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(placesRcView)
        val behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL
        val onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                val place = places[position]
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(place.latitude, place.longitude)))
            }
        }
        val snapOnScrollListener = SnapOnScrollListener(helper, behavior, onSnapPositionChangeListener)
        placesRcView.addOnScrollListener(snapOnScrollListener)
    }

    @SuppressLint("RestrictedApi")
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)

        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this.requireActivity()).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this.activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(this.requireActivity()) { task: Task<Location> ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result!!
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(lastLocation!!.latitude, lastLocation!!.longitude))
                    .zoom(17f)
                    .build()
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else Toast.makeText(this.requireContext(), "No current location found", Toast.LENGTH_LONG).show()
        }
    }

    private fun givePermission() = Dexter.withActivity(this.activity)
        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        .withListener(this)
        .check()

    private fun isPermissionGiven(): Boolean = ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    override fun onPermissionGranted(response: PermissionGrantedResponse?) = getCurrentLocation()

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) = token!!.continuePermissionRequest()

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this.context, "Permission required for showing location", Toast.LENGTH_LONG).show()
        activity?.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> if (resultCode == Activity.RESULT_OK) getCurrentLocation()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
