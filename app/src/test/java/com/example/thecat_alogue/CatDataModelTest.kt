package com.example.thecat_alogue

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CatDataModelTest {

    @Test
    fun testCatImageSettersAndGetters() {
        val image = CatImage(id = "test_cat_123", url = "https://cdn2.thecatapi.com/images/test.jpg")
        assertEquals("test_cat_123", image.id)
        assertEquals("https://cdn2.thecatapi.com/images/test.jpg", image.url)
    }

    @Test
    fun testBreedModelCreation() {
        val breed = Breed(id = "beng", name = "Bengal", origin = "United States")
        assertNotNull(breed)
        assertEquals("beng", breed.id)
        assertEquals("Bengal", breed.name)
        assertEquals("Bengal", breed.toString())
    }

    @Test
    fun testResourceWrapper() {
        val loading = Resource.loading<String>()
        assertTrue(loading.isLoading)
        assertFalse(loading.isSuccess)
        assertFalse(loading.isError)

        val success = Resource.success("Cats Loaded!")
        assertTrue(success.isSuccess)
        assertEquals("Cats Loaded!", success.data)

        val error = Resource.error<String>("Network Failed")
        assertTrue(error.isError)
        assertEquals("Network Failed", error.message)
    }

    @Test
    fun testCatUtilsGreetingAndShareMessage() {
        assertEquals(R.string.good_morning, CatUtils.getTimeGreeting(8))
        assertEquals(R.string.good_afternoon, CatUtils.getTimeGreeting(14))
        assertEquals(R.string.good_evening, CatUtils.getTimeGreeting(18))
        assertEquals(R.string.hello_night_owl, CatUtils.getTimeGreeting(23))

        val url = "https://cdn2.thecatapi.com/images/test.gif"
        assertEquals("Check out this cat!\nhttps://cdn2.thecatapi.com/images/test.gif", CatUtils.formatShareMessage(url))
    }

    @Test
    fun testCatFactAndEntities() {
        val fact = CatFact(fact = "Cats sleep 70% of their day", length = 27)
        assertEquals("Cats sleep 70% of their day", fact.fact)
        assertEquals(27, fact.length)

        val breedEntity = BreedEntity("siam", "Siamese", origin = "Thailand")
        val domainBreed = breedEntity.toDomainModel()
        assertEquals("siam", domainBreed.id)
        assertEquals("Siamese", domainBreed.name)

        val catEntity = CachedCatEntity("c101", "https://cat.com/1.jpg")
        val domainCat = catEntity.toDomainModel()
        assertEquals("c101", domainCat.id)

        val bookmark = BookmarkEntity("b1", "https://cat.com/b1.jpg", "My Kittens")
        assertEquals("b1", bookmark.id)
        assertEquals("My Kittens", bookmark.collectionName)
    }
}
