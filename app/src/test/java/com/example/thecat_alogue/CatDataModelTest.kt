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
}
