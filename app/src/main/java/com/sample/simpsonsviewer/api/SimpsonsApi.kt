package com.sample.simpsonsviewer.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SimpsonsApi {

    companion object {

        private var INSTANCE: Retrofit? = null

        fun getInstance(): SimpsonsService {
            var temp = INSTANCE
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Retrofit.Builder()
                        .baseUrl(ApiUtil.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    temp = INSTANCE
                }
            }

            return temp!!.create(SimpsonsService::class.java)
        }

    }
}

object ApiUtil {

    // https://api.duckduckgo.com/?q=simpsons+characters&format=json&atb=v357-1
    // for images
    // https://duckduckgo.com/i/f0eb79ee.png

     const val BASE_URL: String = "https://api.duckduckgo.com/"
}