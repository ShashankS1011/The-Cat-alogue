package com.example.thecat_alogue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CatRepository = CatRepository.getInstance().apply {
        initDatabase(application.applicationContext)
    }

    private val _catsUiState = MutableStateFlow<Resource<List<CatImage>>>(Resource.loading())
    val catsUiState: StateFlow<Resource<List<CatImage>>> = _catsUiState.asStateFlow()

    private val _breedsUiState = MutableStateFlow<Resource<List<Breed>>>(Resource.loading())
    val breedsUiState: StateFlow<Resource<List<Breed>>> = _breedsUiState.asStateFlow()

    private val _catFactState = MutableStateFlow("")
    val catFactState: StateFlow<String> = _catFactState.asStateFlow()

    private val _toastMessageEvent = MutableSharedFlow<String>()
    val toastMessageEvent: SharedFlow<String> = _toastMessageEvent.asSharedFlow()

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    var currentBreedId: String? = null
    private var isLoading = false
    private var currentCatList = mutableListOf<CatImage>()

    fun setOffline(offline: Boolean) {
        _isOffline.value = offline
    }

    fun loadCats(isPagination: Boolean = false) {
        if (isLoading) return
        isLoading = true

        if (!isPagination) {
            _catsUiState.value = Resource.loading()
        }

        viewModelScope.launch {
            val result = repository.getCats(currentBreedId, 50)
            isLoading = false
            result.fold(
                onSuccess = { newCats ->
                    if (!isPagination) {
                        currentCatList = newCats.toMutableList()
                    } else {
                        currentCatList.addAll(newCats)
                    }
                    _catsUiState.value = Resource.success(currentCatList.toList())
                },
                onFailure = { error ->
                    _catsUiState.value = Resource.error(error.localizedMessage ?: "Failed to load cats")
                }
            )
        }
    }

    fun loadBreeds() {
        _breedsUiState.value = Resource.loading()
        viewModelScope.launch {
            val result = repository.getBreeds()
            result.fold(
                onSuccess = { breeds ->
                    _breedsUiState.value = Resource.success(breeds)
                },
                onFailure = { error ->
                    _breedsUiState.value = Resource.error(error.localizedMessage ?: "Failed to load breeds")
                }
            )
        }
    }

    fun loadCatFact() {
        viewModelScope.launch {
            val fact = repository.getRandomCatFact()
            _catFactState.value = fact
        }
    }
}
