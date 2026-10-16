package com.example.thecat_alogue;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CatApiService {

    // 1. Get Images (Random OR by Breed)
    // NOTE: removed "v1/" from the path because Base URL usually includes it or ends in "/"
    // If your Base URL is "https://api.thecatapi.com/v1/", then use "images/search"
    // If your Base URL is "https://api.thecatapi.com/", then use "v1/images/search"
    // I kept your "v1/" paths assuming your Base URL is just "https://api.thecatapi.com/"
    @GET("v1/images/search")
    Call<List<CatImage>> getCats(
            @Header("x-api-key") String apiKey,
            @Query("limit") int limit,
            @Query("breed_ids") String breedId
    );

    // 2. Get List of ALL Breeds (For the Dropdown Spinner)
    @GET("v1/breeds")
    Call<List<Breed>> getBreeds(
            @Header("x-api-key") String apiKey
    );

    // 3. Search Breeds by Text (Optional - kept for safety)
    @GET("v1/breeds/search")
    Call<List<BreedResponse>> searchBreeds(
            @Header("x-api-key") String apiKey,
            @Query("q") String query
    );

    // 4. Add to Favourites
    // CHANGED: Return 'FavouriteResponse' instead of 'ResponseBody' so you can parse the ID on success
    @POST("v1/favourites")
    Call<FavouriteResponse> addFavourite(
            @Header("x-api-key") String apiKey,
            @Body FavouriteRequest body
    );

    // 5. Get My Favourites
    @GET("v1/favourites")
    Call<List<FavouriteResponse>> getFavourites(
            @Header("x-api-key") String apiKey,
            @Query("sub_id") String subId
    );

    // 6. Delete a Favourite (New Feature - You will need this later!)
    // DELETE requires the 'favourite_id' you get from the response of step 4 or 5
    @retrofit2.http.DELETE("v1/favourites/{favourite_id}")
    Call<ResponseBody> deleteFavourite(
            @Header("x-api-key") String apiKey,
            @retrofit2.http.Path("favourite_id") String favouriteId
    );
}