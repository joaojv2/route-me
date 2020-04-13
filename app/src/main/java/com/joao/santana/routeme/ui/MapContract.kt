package com.joao.santana.routeme.ui

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.joao.santana.routeme.models.Directions
import org.koin.core.KoinComponent

interface MapContract : KoinComponent {

    interface View: MapContract, OnMapReadyCallback, PlaceSelectionListener, GoogleApiClient.ConnectionCallbacks {
        suspend fun onDirectionsSuccess(directions: Directions, start: LatLng, end: LatLng)
    }

    interface Presenter: MapContract {
        fun onConnected(): LocationRequest
        fun drawRoute(directions: Directions): PolylineOptions
        fun getDirections(apiKey: String, start: LatLng, end: LatLng)
        fun approximateRoute(start: LatLng, end: LatLng): CameraUpdate
    }
}