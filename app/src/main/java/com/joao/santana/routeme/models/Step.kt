package com.joao.santana.routeme.models

import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName(POLYLINE) val polyline: Polyline
) {
    private companion object {
        private const val POLYLINE: String = "polyline"
    }
}