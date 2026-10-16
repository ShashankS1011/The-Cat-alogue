package com.example.thecat_alogue;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatRepository {

    private static final String BASE_URL = "https://api.thecatapi.com/";
    private static CatRepository instance;
    private final CatApiService apiService;

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    private CatRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(CatApiService.class);
    }

    public static synchronized CatRepository getInstance() {
        if (instance == null) {
            instance = new CatRepository();
        }
        return instance;
    }

    public CatApiService getApiService() {
        return apiService;
    }

    public String getApiKey() {
        return BuildConfig.CAT_API_KEY;
    }

    public void getCats(String breedId, int limit, RepositoryCallback<List<CatImage>> callback) {
        apiService.getCats(getApiKey(), limit, breedId).enqueue(new Callback<List<CatImage>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatImage>> call, @NonNull Response<List<CatImage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch cats. HTTP Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatImage>> call, @NonNull Throwable t) {
                callback.onError("Network failure: " + (t.getMessage() != null ? t.getMessage() : "Unknown error"));
            }
        });
    }

    public void getBreeds(RepositoryCallback<List<Breed>> callback) {
        apiService.getBreeds(getApiKey()).enqueue(new Callback<List<Breed>>() {
            @Override
            public void onResponse(@NonNull Call<List<Breed>> call, @NonNull Response<List<Breed>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch breeds.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Breed>> call, @NonNull Throwable t) {
                callback.onError("Network failure loading breeds.");
            }
        });
    }

    public void getFavourites(String userId, RepositoryCallback<List<CatImage>> callback) {
        apiService.getFavourites(getApiKey(), userId).enqueue(new Callback<List<FavouriteResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<FavouriteResponse>> call, @NonNull Response<List<FavouriteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CatImage> favImages = new ArrayList<>();
                    for (FavouriteResponse fav : response.body()) {
                        if (fav != null && fav.getImage() != null) {
                            favImages.add(fav.getImage());
                        }
                    }
                    callback.onSuccess(favImages);
                } else {
                    callback.onError("Failed to fetch favourites.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FavouriteResponse>> call, @NonNull Throwable t) {
                callback.onError("Network error fetching favourites.");
            }
        });
    }

    public void addFavourite(String imageId, String userId, RepositoryCallback<Boolean> callback) {
        FavouriteRequest req = new FavouriteRequest(imageId, userId);
        apiService.addFavourite(getApiKey(), req).enqueue(new Callback<FavouriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavouriteResponse> call, @NonNull Response<FavouriteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Error adding favourite: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavouriteResponse> call, @NonNull Throwable t) {
                callback.onError("Network error saving favourite.");
            }
        });
    }
}
