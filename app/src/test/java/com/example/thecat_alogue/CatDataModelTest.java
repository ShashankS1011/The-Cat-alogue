package com.example.thecat_alogue;

import org.junit.Test;
import static org.junit.Assert.*;

public class CatDataModelTest {

    @Test
    public void testCatImageSettersAndGetters() {
        CatImage image = new CatImage();
        image.setId("test_cat_123");
        image.setUrl("https://cdn2.thecatapi.com/images/test.jpg");

        assertEquals("test_cat_123", image.getId());
        assertEquals("https://cdn2.thecatapi.com/images/test.jpg", image.getUrl());
    }

    @Test
    public void testFavouriteRequestCreation() {
        FavouriteRequest request = new FavouriteRequest("cat_456", "user_101");
        assertNotNull(request);
    }

    @Test
    public void testResourceWrapper() {
        Resource<String> loading = Resource.loading();
        assertTrue(loading.isLoading());
        assertFalse(loading.isSuccess());
        assertFalse(loading.isError());

        Resource<String> success = Resource.success("Cats Loaded!");
        assertTrue(success.isSuccess());
        assertEquals("Cats Loaded!", success.getData());

        Resource<String> error = Resource.error("Network Failed");
        assertTrue(error.isError());
        assertEquals("Network Failed", error.getMessage());
    }
}
