package com.joao.santana.routeme.extensions

import com.google.android.gms.maps.model.LatLng

fun LatLng.toParam(): String = "$latitude,$longitude"