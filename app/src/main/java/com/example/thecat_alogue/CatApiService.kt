package com.example.thecat_alogue

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CatApiService {

    // 1. Get Images (Random OR by Breed)
    @GET("v1/images/search")
    suspend fun getCats(
        @Header("x-api-key") apiKey: String,
        @Query("limit") limit: Int,
        @Query("breed_ids") breedId: String? = null
    ): List<CatImage>

    // 2. Get List of ALL Breeds
    @GET("v1/breeds")
    suspend fun getBreeds(
        @Header("x-api-key") apiKey: String
    ): List<Breed>

    // 3. Search Breeds by Text
    @GET("v1/breeds/search")
    suspend fun searchBreeds(
        @Header("x-api-key") apiKey: String,
        @Query("q") query: String
    ): List<BreedResponse>

    // 4. Get Daily Cat Fact (Cat Facts API)
    @GET("https://catfact.ninja/fact")
    suspend fun getRandomCatFact(): CatFact
}
