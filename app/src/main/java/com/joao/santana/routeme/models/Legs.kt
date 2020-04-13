package com.joao.santana.routeme.models

import com.google.gson.annotations.SerializedName

data class Legs(
    @SerializedName(STEPS) val steps: List<Step>
) {
    private companion object {
        private const val STEPS: String = "steps"
    }
}