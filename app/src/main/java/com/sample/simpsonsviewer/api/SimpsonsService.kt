package com.sample.simpsonsviewer.api

import com.sample.simpsonsviewer.data.model.Characters
import retrofit2.Response
import retrofit2.http.GET

interface SimpsonsService {

    // https://api.duckduckgo.com/?q=simpsons+characters&format=json&atb=v357-1
    @GET("?q=simpsons+characters&format=json&atb=v357-1")
    suspend fun getSimpsonsCharacters():Response<Characters>
}