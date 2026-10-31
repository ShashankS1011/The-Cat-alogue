package com.example.thecat_alogue

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
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

    // 4. Add to Favourites
    @POST("v1/favourites")
    suspend fun addFavourite(
        @Header("x-api-key") apiKey: String,
        @Body body: FavouriteRequest
    ): FavouriteResponse

    // 5. Get My Favourites
    @GET("v1/favourites")
    suspend fun getFavourites(
        @Header("x-api-key") apiKey: String,
        @Query("sub_id") subId: String
    ): List<FavouriteResponse>

    // 6. Delete a Favourite
    @DELETE("v1/favourites/{favourite_id}")
    suspend fun deleteFavourite(
        @Header("x-api-key") apiKey: String,
        @Path("favourite_id") favouriteId: String
    ): ResponseBody

    // 7. Get Daily Cat Fact (Cat Facts API)
    @GET("https://catfact.ninja/fact")
    suspend fun getRandomCatFact(): CatFact
}
