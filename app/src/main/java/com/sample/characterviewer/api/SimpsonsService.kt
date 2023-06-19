package com.sample.characterviewer.api

import com.sample.characterviewer.data.model.Characters
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface SimpsonsService {

    // "?q=simpsons+characters&format=json&atb=v357-1"
    // https://api.duckduckgo.com/?q=simpsons+characters&format=json&atb=v357-1
    @GET("")
    suspend fun getSimpsonsCharacters(@Url chars:String):Response<Characters>

//    @GET("?q=")
//    suspend fun getSimpsonsCharacters(@Path("characters") chars:String):Response<Characters>
}