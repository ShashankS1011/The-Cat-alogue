package com.example.thecat_alogue;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatRepository {

    private static final String BASE_URL = "https://api.thecatapi.com/";
    private static CatRepository instance;
    private final CatApiService apiService;
    private final ExecutorService executorService;
    private AppDatabase database;

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
        executorService = Executors.newFixedThreadPool(4);
    }

    public static synchronized CatRepository getInstance() {
        if (instance == null) {
            instance = new CatRepository();
        }
        return instance;
    }

    public void initDatabase(Context context) {
        if (database == null) {
            database = AppDatabase.getInstance(context);
        }
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

    public void getRandomCatFact(RepositoryCallback<String> callback) {
        apiService.getRandomCatFact().enqueue(new Callback<CatFact>() {
            @Override
            public void onResponse(@NonNull Call<CatFact> call, @NonNull Response<CatFact> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getFact() != null) {
                    callback.onSuccess(response.body().getFact());
                } else {
                    callback.onError("Cats spend 70% of their lives sleeping!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CatFact> call, @NonNull Throwable t) {
                callback.onSuccess("Cats have 32 muscles in each ear!");
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
                            saveFavoriteToRoom(fav.getImage().getId(), fav.getImage().getUrl());
                        }
                    }
                    callback.onSuccess(favImages);
                } else {
                    loadFavoritesFromRoom(callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FavouriteResponse>> call, @NonNull Throwable t) {
                loadFavoritesFromRoom(callback);
            }
        });
    }

    private void loadFavoritesFromRoom(RepositoryCallback<List<CatImage>> callback) {
        if (database == null) {
            callback.onError("No offline data available.");
            return;
        }

        executorService.execute(() -> {
            List<FavoriteCatEntity> entities = database.favoriteCatDao().getAllFavoritesSync();
            List<CatImage> offlineCats = new ArrayList<>();
            for (FavoriteCatEntity entity : entities) {
                CatImage cat = new CatImage();
                cat.setId(entity.getId());
                cat.setUrl(entity.getUrl());
                offlineCats.add(cat);
            }
            callback.onSuccess(offlineCats);
        });
    }

    public void addFavourite(CatImage cat, String userId, RepositoryCallback<Boolean> callback) {
        saveFavoriteToRoom(cat.getId(), cat.getUrl());

        FavouriteRequest req = new FavouriteRequest(cat.getId(), userId);
        apiService.addFavourite(getApiKey(), req).enqueue(new Callback<FavouriteResponse>() {
            @Override
            public void onResponse(@NonNull Call<FavouriteResponse> call, @NonNull Response<FavouriteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(true);
                } else {
                    callback.onSuccess(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavouriteResponse> call, @NonNull Throwable t) {
                callback.onSuccess(true);
            }
        });
    }

    public void removeFavourite(String imageId, RepositoryCallback<Boolean> callback) {
        if (database != null) {
            executorService.execute(() -> database.favoriteCatDao().deleteById(imageId));
        }
        callback.onSuccess(true);
    }

    private void saveFavoriteToRoom(String id, String url) {
        if (database != null && id != null && url != null) {
            executorService.execute(() -> {
                FavoriteCatEntity entity = new FavoriteCatEntity(id, url, System.currentTimeMillis());
                database.favoriteCatDao().insert(entity);
            });
        }
    }
}
