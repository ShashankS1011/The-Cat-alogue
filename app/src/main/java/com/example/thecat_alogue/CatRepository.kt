package com.example.thecat_alogue

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CatRepository private constructor() {

    private val baseUrl = "https://api.thecatapi.com/"
    val apiService: CatApiService
    private var database: AppDatabase? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(CatApiService::class.java)
    }

    fun initDatabase(context: Context) {
        if (database == null) {
            database = AppDatabase.getInstance(context)
        }
    }

    fun getApiKey(): String {
        return BuildConfig.CAT_API_KEY
    }

    suspend fun getCats(breedId: String?, limit: Int): Result<List<CatImage>> = withContext(Dispatchers.IO) {
        try {
            val remoteCats = apiService.getCats(getApiKey(), limit, breedId)
            // Cache loaded cats into Room DB for offline viewing
            val cachedEntities = remoteCats.mapNotNull { CachedCatEntity.fromDomainModel(it, breedId) }
            if (cachedEntities.isNotEmpty()) {
                database?.cachedCatDao()?.insertAll(cachedEntities)
            }
            Result.success(remoteCats)
        } catch (e: Exception) {
            // Fallback to offline cached feed
            val cachedCats = database?.cachedCatDao()?.getCachedCats(breedId, limit) ?: emptyList()
            if (cachedCats.isNotEmpty()) {
                Result.success(cachedCats.map { it.toDomainModel() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getBreeds(): Result<List<Breed>> = withContext(Dispatchers.IO) {
        try {
            val remoteBreeds = apiService.getBreeds(getApiKey())
            val entities = remoteBreeds.map { BreedEntity.fromDomainModel(it) }
            database?.breedDao()?.insertAll(entities)
            Result.success(remoteBreeds)
        } catch (e: Exception) {
            val cachedBreeds = database?.breedDao()?.getAllBreedsSync() ?: emptyList()
            if (cachedBreeds.isNotEmpty()) {
                Result.success(cachedBreeds.map { it.toDomainModel() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getRandomCatFact(): String = withContext(Dispatchers.IO) {
        try {
            val catFact = apiService.getRandomCatFact()
            catFact.fact ?: "Cats spend 70% of their lives sleeping!"
        } catch (e: Exception) {
            "Cats spend 70% of their lives sleeping!"
        }
    }

    suspend fun getFavourites(userId: String): Result<List<CatImage>> = withContext(Dispatchers.IO) {
        val localFavs = database?.favoriteCatDao()?.getAllFavoritesSync() ?: emptyList()
        val images = localFavs.map { CatImage(id = it.id, url = it.url) }
        Result.success(images)
    }

    suspend fun addFavourite(cat: CatImage, userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val catId = cat.id ?: return@withContext Result.failure(IllegalArgumentException("Cat ID missing"))
        val catUrl = cat.url ?: return@withContext Result.failure(IllegalArgumentException("Cat URL missing"))

        saveFavoriteToRoomInternal(catId, catUrl)

        if (getApiKey().isNotBlank()) {
            try {
                val request = FavouriteRequest(catId, userId)
                apiService.addFavourite(getApiKey(), request)
            } catch (ignored: Exception) {}
        }
        Result.success(true)
    }

    suspend fun removeFavourite(imageId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        database?.favoriteCatDao()?.deleteById(imageId)
        Result.success(true)
    }

    suspend fun clearAllFavourites(): Result<Boolean> = withContext(Dispatchers.IO) {
        database?.favoriteCatDao()?.deleteAll()
        Result.success(true)
    }

    private suspend fun saveFavoriteToRoomInternal(id: String, url: String) {
        database?.favoriteCatDao()?.insert(FavoriteCatEntity(id, url, System.currentTimeMillis()))
    }

    companion object {
        @Volatile
        private var instance: CatRepository? = null

        @JvmStatic
        fun getInstance(): CatRepository {
            return instance ?: synchronized(this) {
                val inst = CatRepository()
                instance = inst
                inst
            }
        }
    }
}
