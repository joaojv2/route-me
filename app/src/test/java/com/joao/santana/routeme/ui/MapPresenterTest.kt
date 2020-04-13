package com.joao.santana.routeme.ui

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.joao.santana.routeme.R
import com.joao.santana.routeme.extensions.toParam
import com.joao.santana.routeme.models.Directions
import com.joao.santana.routeme.models.Polyline
import com.joao.santana.routeme.services.DirectionsService
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Response

class MapPresenterTest {

    private val view: MapContract.View = mockk()
    private val service: DirectionsService = mockk()

    private lateinit var presenter: MapContract.Presenter

    @Before
    fun init() {
        presenter = MapPresenter(view, service)
    }

    @Test
    fun `should create location request on connected`() {
        val locationRequest = presenter.onConnected()

        assert(locationRequest.interval == MapPresenter.INTERVAL)
        assert(locationRequest.fastestInterval == MapPresenter.INTERVAL)
        assert(locationRequest.priority == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
    }

    @Test
    fun `should create polyline options to draw route`() {
        val directions = mockk<Directions>(relaxed = true)

        val latLong = LatLng(10.0, 10.0)
        val points = PolyUtil.encode(listOf(latLong))

        every { directions.routes } returns listOf(mockk())
        every { directions.routes[0].legs } returns listOf(mockk())
        every { directions.routes[0].legs[0].steps } returns listOf(mockk())
        every { directions.routes[0].legs[0].steps[0].polyline } returns Polyline(points)

        val polylineOptions = presenter.drawRoute(directions)

        assert(polylineOptions.color == R.color.quantum_bluegrey100)
        assert(polylineOptions.width == MapPresenter.DRAW_WIDTH)
    }

    @Test
    fun `should call directions and return successfully response`() {
        val apiKey = ""
        val start = LatLng(10.0, 10.0)
        val end = LatLng(20.0, 20.0)

        val deferred = CompletableDeferred<Response<Directions>>()
        val directions = mockk<Directions>()

        deferred.complete(Response.success(directions))

        coEvery { view.onDirectionsSuccess(directions, start, end) } just runs

        every { service.directionsAsync(apiKey, any(), start.toParam(), end.toParam()) } returns deferred

        presenter.getDirections(apiKey, start, end)

        coVerify {
            service.directionsAsync(apiKey, any(), start.toParam(), end.toParam())
            view.onDirectionsSuccess(directions, start, end)
        }
    }

    @Test
    fun `should call directions and return error response`() {
        val apiKey = ""
        val start = LatLng(10.0, 10.0)
        val end = LatLng(20.0, 20.0)

        val deferred = CompletableDeferred<Response<Directions>>()
        val code = 500

        deferred.complete(Response.error(code, mockk(relaxed = true)))

        coEvery { view.onError(any()) } just runs

        every { service.directionsAsync(apiKey, any(), start.toParam(), end.toParam()) } returns deferred

        presenter.getDirections(apiKey, start, end)

        coVerify {
            service.directionsAsync(apiKey, any(), start.toParam(), end.toParam())
            view.onError(any())
        }
    }

    companion object {

        @JvmStatic
        @BeforeClass
        fun preSetUp() {
            mockkStatic(PolyUtil::class)
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            unmockkAll()
        }
    }
}