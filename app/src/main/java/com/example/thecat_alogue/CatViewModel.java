package com.example.thecat_alogue;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class CatViewModel extends AndroidViewModel {

    private static final String DEFAULT_USER_ID = "user_123";

    private final CatRepository repository;
    private final MutableLiveData<Resource<List<CatImage>>> catsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Breed>>> breedsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessageLiveData = new MutableLiveData<>();

    private String currentBreedId = null;
    private boolean isLoading = false;
    private List<CatImage> currentCatList = new ArrayList<>();

    public CatViewModel(@NonNull Application application) {
        super(application);
        this.repository = CatRepository.getInstance();
        this.repository.initDatabase(application.getApplicationContext());
    }

    public LiveData<Resource<List<CatImage>>> getCatsLiveData() {
        return catsLiveData;
    }

    public LiveData<Resource<List<Breed>>> getBreedsLiveData() {
        return breedsLiveData;
    }

    public LiveData<String> getToastMessageLiveData() {
        return toastMessageLiveData;
    }

    public String getCurrentBreedId() {
        return currentBreedId;
    }

    public void setCurrentBreedId(String breedId) {
        this.currentBreedId = breedId;
    }

    public List<CatImage> getCurrentCatList() {
        return currentCatList;
    }

    public void loadCats(boolean isPagination) {
        if (isLoading) return;
        isLoading = true;

        if (!isPagination) {
            catsLiveData.setValue(Resource.loading());
        }

        repository.getCats(currentBreedId, 50, new CatRepository.RepositoryCallback<List<CatImage>>() {
            @Override
            public void onSuccess(List<CatImage> newCats) {
                isLoading = false;
                if (!isPagination) {
                    currentCatList = new ArrayList<>(newCats);
                } else {
                    currentCatList.addAll(newCats);
                }
                catsLiveData.setValue(Resource.success(currentCatList));
            }

            @Override
            public void onError(String errorMessage) {
                isLoading = false;
                catsLiveData.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadBreeds() {
        breedsLiveData.setValue(Resource.loading());
        repository.getBreeds(new CatRepository.RepositoryCallback<List<Breed>>() {
            @Override
            public void onSuccess(List<Breed> breeds) {
                breedsLiveData.setValue(Resource.success(breeds));
            }

            @Override
            public void onError(String errorMessage) {
                breedsLiveData.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadFavourites() {
        catsLiveData.setValue(Resource.loading());
        repository.getFavourites(DEFAULT_USER_ID, new CatRepository.RepositoryCallback<List<CatImage>>() {
            @Override
            public void onSuccess(List<CatImage> favCats) {
                currentCatList = new ArrayList<>(favCats);
                catsLiveData.setValue(Resource.success(currentCatList));
            }

            @Override
            public void onError(String errorMessage) {
                catsLiveData.setValue(Resource.error(errorMessage));
            }
        });
    }

    public void addFavourite(CatImage cat) {
        repository.addFavourite(cat, DEFAULT_USER_ID, new CatRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                toastMessageLiveData.setValue("Saved to Favourites!");
            }

            @Override
            public void onError(String errorMessage) {
                toastMessageLiveData.setValue(errorMessage);
            }
        });
    }

    public void removeFavourite(String imageId) {
        repository.removeFavourite(imageId, new CatRepository.RepositoryCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                toastMessageLiveData.setValue("Removed from Favourites!");
                loadFavourites(); // Refresh list
            }

            @Override
            public void onError(String errorMessage) {
                toastMessageLiveData.setValue(errorMessage);
            }
        });
    }
}
