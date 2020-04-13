package com.joao.santana.routeme.models

import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName(LEGS) val legs: List<Legs>
) {
    private companion object {
        private const val LEGS: String = "legs"
    }
}