package com.joao.santana.routeme.services

import com.joao.santana.routeme.models.Directions
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsService {

    @GET(DIRECTIONS_ENDPOINT)
    fun directionsAsync(
        @Query(QUERY_KEY) key: String,
        @Query(QUERY_MODE) mode: String,
        @Query(QUERY_ORIGIN) origin: String,
        @Query(QUERY_DESTINATION) destination: String
    ): Deferred<Response<Directions>>

    private companion object {
        private const val DIRECTIONS_ENDPOINT: String = "maps/api/directions/json"

        private const val QUERY_KEY: String = "key"
        private const val QUERY_MODE: String = "mode"
        private const val QUERY_ORIGIN: String = "origin"
        private const val QUERY_DESTINATION: String = "destination"
    }
}