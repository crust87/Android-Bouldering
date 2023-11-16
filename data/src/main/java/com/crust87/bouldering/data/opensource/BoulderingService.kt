package com.crust87.bouldering.data.opensource

import com.crust87.bouldering.data.opensource.type.OpenSourceLicense
import retrofit2.http.GET
import retrofit2.http.Path

interface BoulderingService {
    @GET("bouldering/{platform}/opensource.json")
    suspend fun listOpenSource(@Path("platform") platform: String): List<OpenSourceLicense>
}