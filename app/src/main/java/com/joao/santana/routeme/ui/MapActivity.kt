package com.joao.santana.routeme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.joao.santana.routeme.R
import com.joao.santana.routeme.models.Directions
import com.joao.santana.routeme.services.DirectionsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.parameter.parametersOf
import kotlin.properties.Delegates


class MapActivity : FragmentActivity(), MapContract.View {

    private val presenter: MapContract.Presenter by inject { parametersOf(this) }

    private var googleMap: GoogleMap by Delegates.notNull()
    private var locationManager: LocationManager by Delegates.notNull()
    private var googleApiClient: GoogleApiClient by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        supportFragmentManager.findFragmentById(R.id.google_maps)
            .let { it as? SupportMapFragment }
            ?.apply { getMapAsync(this@MapActivity) }

        supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
            .let { it as? AutocompleteSupportFragment }
            ?.apply {
                setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                setOnPlaceSelectedListener(this@MapActivity)
            }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let { googleMap = it }

        if (SDK_INT >= Build.VERSION_CODES.M) {
            takeIf { isPermissionGranted() }
                ?.apply { onPermissionGranted() }
                ?: onPermissionNotGranted()
        } else {
            onPermissionGranted()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPlaceSelected(place: Place) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(place.latLng!!))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.latLng))

        presenter.getDirections(getString(R.string.google_maps_key), getLocation(), place.latLng!!)
    }

    override suspend fun onDirectionsSuccess(directions: Directions, start: LatLng, end: LatLng) {
        withContext(Dispatchers.Main) {
            googleMap.addPolyline(presenter.drawRoute(directions))
            googleMap.animateCamera(presenter.approximateRoute(start, end))
        }
    }

    override fun onError(status: Status) {
        Log.i(TAG, "An error occurred: $status")
    }

    override fun onConnected(p0: Bundle?) {
        LocationRequest().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!isPermissionGranted()) {
            if (requestCode == REQUEST_ACCESS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun buildGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener {  }
            .addApi(LocationServices.API)
            .build()

        googleApiClient.connect()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun onPermissionGranted() {
        buildGoogleApiClient()
        googleMap.isMyLocationEnabled = true
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocation(), ZOOM))
    }

    private fun onPermissionNotGranted() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_ACCESS_PERMISSION
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): LatLng {
        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
            return LatLng(it.latitude, it.longitude)
        }

        return LatLng(0.0, 0.0)
    }

    companion object {
        private const val TAG: String = "ROUTE_ME"

        private const val ZOOM: Float = 16.0F
        private const val REQUEST_ACCESS_PERMISSION: Int = 1023
    }
}
