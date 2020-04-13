package com.joao.santana.routeme.models

import com.google.gson.annotations.SerializedName

data class Directions(
    @SerializedName(ROUTES) val routes: List<Route>
) {
    private companion object {
        private const val ROUTES: String = "routes"
    }
}