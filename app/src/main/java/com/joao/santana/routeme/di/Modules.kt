package com.joao.santana.routeme.di

import com.joao.santana.routeme.builders.RetrofitBuilder
import com.joao.santana.routeme.helpers.InterceptorHelper
import com.joao.santana.routeme.helpers.MoshiHelper
import com.joao.santana.routeme.services.DirectionsService
import com.joao.santana.routeme.ui.MapContract
import com.joao.santana.routeme.ui.MapPresenter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object Modules {

    val modules = module {

        single { MoshiHelper() }
        single { InterceptorHelper() }

        single<DirectionsService> { RetrofitBuilder(get(), get()).build(androidApplication()) }

        factory<MapContract.Presenter> { (view: MapContract.View) -> MapPresenter(view, get()) }
    }
}