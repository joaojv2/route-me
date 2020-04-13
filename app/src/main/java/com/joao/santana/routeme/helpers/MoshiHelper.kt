package com.joao.santana.routeme.helpers

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MoshiHelper {

    fun build(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}