package com.joao.santana.routeme.models

import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName(POINTS) val points: String
) {
    private companion object {
        private const val POINTS: String = "points"
    }
}