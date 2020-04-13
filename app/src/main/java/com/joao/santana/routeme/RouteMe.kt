package com.joao.santana.routeme

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.joao.santana.routeme.di.Modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RouteMe : Application() {

    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }

        initInjections()
    }

    private fun initInjections() {
        startKoin { ->
            androidLogger()
            androidContext(this@RouteMe)
            modules(Modules.modules)
        }
    }
}