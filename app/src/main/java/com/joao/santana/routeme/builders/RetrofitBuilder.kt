package com.joao.santana.routeme.builders

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.joao.santana.routeme.R
import com.joao.santana.routeme.helpers.InterceptorHelper
import com.joao.santana.routeme.helpers.MoshiHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitBuilder(
    val moshi: MoshiHelper,
    val interceptorHelper: InterceptorHelper
) {

    inline fun <reified T> build(context: Context): T {
        return OkHttpClient.Builder()
            .addInterceptor(interceptorHelper.loggingInterceptor())
            .build()
            .let { build(context, it) }
    }

    inline fun <reified T> build(context: Context, client: OkHttpClient): T {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi.build()))
            .build()
            .create(T::class.java)
    }
}