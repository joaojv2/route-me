package com.joao.santana.routeme.ui

import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.joao.santana.routeme.R
import com.joao.santana.routeme.models.Directions
import com.joao.santana.routeme.services.DirectionsService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response

class MapPresenter(
    private val view: MapContract.View,
    private val directionsService: DirectionsService
) : MapContract.Presenter {

    override fun onConnected(): LocationRequest {
        return LocationRequest().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
    }

    override fun drawRoute(directions: Directions): PolylineOptions {
        return PolylineOptions().apply {
            directions.routes[0].legs[0].steps.forEach { step ->
                add(PolyUtil.decode(step.polyline.points)[0])
            }

            color(R.color.quantum_bluegrey100).width(DRAW_WIDTH)
        }
    }

    override fun getDirections(apiKey: String, start: LatLng, end: LatLng) {
        GlobalScope.launch {
            directionsService.directionsAsync(apiKey, MODE, start.toParam(), end.toParam())
                .await()
                .handleResponse(start, end)
        }
    }

    override fun approximateRoute(start: LatLng, end: LatLng): CameraUpdate {
        return CameraUpdateFactory.newLatLngBounds(
            LatLngBounds.Builder().apply {
                include(start)
                include(end)
            }.build(),
            ROUTE_PADDING_PIXELS
        )
    }

    private suspend fun Response<Directions>.handleResponse(start: LatLng, end: LatLng) {
        when (isSuccessful) {
            true -> view.onDirectionsSuccess(body()!!, start, end)
            false -> view.onError(Status(code(), message()))
        }
    }

    private fun LatLng.toParam(): String = "$latitude,$longitude"

    companion object {
        private const val MODE: String = ""
        private const val DRAW_WIDTH: Float = 10F
        private const val ROUTE_PADDING_PIXELS: Int = 200
    }
}