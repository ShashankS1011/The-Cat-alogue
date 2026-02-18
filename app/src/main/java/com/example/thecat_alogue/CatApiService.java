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
    // We added 'breed_ids' here.
    // If you pass null, it gives random cats.
    // If you pass "beng", it gives Bengals.
    @GET("v1/images/search")
    Call<List<CatImage>> getCats(
            @Header("x-api-key") String apiKey,
            @Query("limit") int limit,
            @Query("breed_ids") String breedId
    );

    // 2. Get List of ALL Breeds (For the Dropdown Spinner)
    // This fetches the list of options (Abyssinian, Aegean, American Bobtail...)
    @GET("v1/breeds")
    Call<List<Breed>> getBreeds(
            @Header("x-api-key") String apiKey
    );

    // 3. Search Breeds by Text (Old method - kept for safety)
    @GET("v1/breeds/search")
    Call<List<BreedResponse>> searchBreeds(
            @Header("x-api-key") String apiKey,
            @Query("q") String query
    );

    // 4. Add to Favourites
    @POST("v1/favourites")
    Call<ResponseBody> addFavourite(
            @Header("x-api-key") String apiKey,
            @Body FavouriteRequest body
    );

    // 5. Get My Favourites
    @GET("v1/favourites")
    Call<List<FavouriteResponse>> getFavourites(
            @Header("x-api-key") String apiKey,
            @Query("sub_id") String subId
    );
}