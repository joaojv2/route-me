package com.joao.santana.routeme.helpers

import okhttp3.logging.HttpLoggingInterceptor

class InterceptorHelper {

    fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { ->
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}