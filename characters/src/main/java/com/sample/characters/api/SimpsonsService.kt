package com.sample.characters.api

import com.sample.characters.data.model.Characters
import retrofit2.Response
import retrofit2.http.GET

interface SimpsonsService {

    // Simpsons: https://api.duckduckgo.com/?q=simpsons+characters&format=json&atb=v357-1
    @GET("?q=simpsons+characters&format=json&atb=v357-1")
    suspend fun getSimpsonsCharacters(): Response<Characters>

    // The Wire: http://api.duckduckgo.com/?q=the+wire+characters&format=json
//    @GET("?q=the+wire+characters&format=json")
//    suspend fun getSimpsonsCharacters():Response<Characters>
}