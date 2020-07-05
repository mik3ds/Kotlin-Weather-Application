package com.mikec.apiweatherapplication

import com.mikec.apiweatherapplication.Models.APIResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface APIInterface {

    @GET("weather.json")
    fun getWeather(): Call<APIResponse>

    companion object{
        private var BASE_URL = "https://dnu5embx6omws.cloudfront.net/venues/"

        fun init():APIInterface{
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIInterface::class.java)
        }
    }
}