package com.example.thecat_alogue

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteCatDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: FavoriteCatDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteCatDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetFavoriteCat() = runBlocking {
        val entity = FavoriteCatEntity("cat_1", "https://example.com/cat1.jpg")
        dao.insert(entity)

        val favorites = dao.getAllFavoritesSync()
        assertEquals(1, favorites.size)
        assertEquals("cat_1", favorites[0].id)
        assertTrue(dao.isFavorite("cat_1"))
    }

    @Test
    fun deleteFavoriteCat() = runBlocking {
        val entity = FavoriteCatEntity("cat_2", "https://example.com/cat2.jpg")
        dao.insert(entity)
        assertTrue(dao.isFavorite("cat_2"))

        dao.deleteById("cat_2")
        assertFalse(dao.isFavorite("cat_2"))
        assertEquals(0, dao.getAllFavoritesSync().size)
    }

    @Test
    fun clearAllFavorites() = runBlocking {
        dao.insert(FavoriteCatEntity("c1", "https://a.com/1.jpg"))
        dao.insert(FavoriteCatEntity("c2", "https://a.com/2.jpg"))

        assertEquals(2, dao.getAllFavoritesSync().size)
        dao.deleteAll()
        assertEquals(0, dao.getAllFavoritesSync().size)
    }
}
