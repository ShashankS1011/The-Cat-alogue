package com.example.thecat_alogue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // TODO: Paste your real API Key here
    private static final String API_KEY = "YOUR_API_KEY_HERE";

    // UI Components
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout searchContainer;
    private Spinner breedSpinner; // Replaces the old EditText
    private TextView txtCredits;

    // Logic & Data
    private CatAdapter adapter;
    private CatApiService apiService;
    private String currentBreedId = null; // null = Random, "beng" = Bengal, etc.
    private boolean isLoading = false;    // Prevents double-loading during scroll

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- 1. Setup Toolbar & Greeting ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupGreeting();

        // --- 2. Setup Navigation Drawer ---
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- 3. Setup UI Elements ---
        searchContainer = findViewById(R.id.searchContainer);
        breedSpinner = findViewById(R.id.breedSpinner);
        txtCredits = findViewById(R.id.txtCredits);

        // --- 4. Setup RecyclerView & Infinite Scroll ---
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // INFINITE SCROLL LISTENER
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // If we can't scroll down anymore (direction 1), we are at the bottom
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    loadCats(true); // Load MORE cats (append)
                }
            }
        });

        // --- 5. Setup Pull-to-Refresh ---
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadCats(false); // Reload FRESH cats (replace)
        });

        // --- 6. Setup Retrofit ---
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.thecatapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(CatApiService.class);

        // --- 7. Initial Data Load ---
        setupBreedSpinner(); // Fetch the list of breeds for the dropdown
        loadCats(false);     // Load initial random cats

        // --- 8. Credits Link ---
        txtCredits.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ShashankS1011"));
            startActivity(browserIntent);
        });

        // --- 9. Back Button Handling ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    // ==========================================
    // CORE LOGIC: Loading Cats
    // ==========================================

    /**
     * The Master Method to load cats.
     * @param isPagination TRUE if we are scrolling down (append data). FALSE if we are refreshing/starting (replace data).
     */
    private void loadCats(boolean isPagination) {
        if (isLoading) return; // Stop if already busy
        isLoading = true;

        if (!isPagination) {
            swipeRefreshLayout.setRefreshing(true); // Show spinner only for refresh
        } else {
            Toast.makeText(this, "Loading more...", Toast.LENGTH_SHORT).show();
        }

        // Call API with the currentBreedId (if null, it gets random cats)
        apiService.getCats(API_KEY, 50, currentBreedId).enqueue(new Callback<List<CatImage>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatImage>> call, @NonNull Response<List<CatImage>> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<CatImage> newCats = response.body();

                    if (!isPagination) {
                        // REFRESH: Create new adapter
                        adapter = new CatAdapter(newCats, apiService, API_KEY);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // PAGINATION: Add to existing list
                        if (adapter != null) {
                            adapter.addData(newCats);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No more cats found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatImage>> call, @NonNull Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // SPINNER LOGIC (Breed Dropdown)
    // ==========================================

    private void setupBreedSpinner() {
        // Fetch list of breeds
        apiService.getBreeds(API_KEY).enqueue(new Callback<List<Breed>>() {
            @Override
            public void onResponse(Call<List<Breed>> call, Response<List<Breed>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Breed> breeds = response.body();

                    // Create Adapter for Spinner
                    ArrayAdapter<Breed> spinnerAdapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            breeds
                    );
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    breedSpinner.setAdapter(spinnerAdapter);

                    // Handle Selection
                    breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // 1. Get selected breed
                            Breed selected = (Breed) parent.getItemAtPosition(position);

                            // 2. Update global ID and reload
                            // Only reload if we are actually in "Search Mode" (search container is visible)
                            if (searchContainer.getVisibility() == View.VISIBLE) {
                                currentBreedId = selected.getId();
                                loadCats(false); // False = Clear list and start fresh
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Breed>> call, Throwable t) {
                // Fail silently or log it
            }
        });
    }

    // ==========================================
    // FAVOURITES LOGIC
    // ==========================================

    private void loadFavourites() {
        Toast.makeText(this, "Loading favourites...", Toast.LENGTH_SHORT).show();
        searchContainer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        apiService.getFavourites(API_KEY, "user_123").enqueue(new Callback<List<FavouriteResponse>>() {
            @Override
            public void onResponse(Call<List<FavouriteResponse>> call, Response<List<FavouriteResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<CatImage> myFavs = new ArrayList<>();
                    for (FavouriteResponse item : response.body()) {
                        if (item.getImage() != null) myFavs.add(item.getImage());
                    }
                    if (myFavs.isEmpty()) Toast.makeText(MainActivity.this, "No favourites yet!", Toast.LENGTH_SHORT).show();

                    // Favourites don't support infinite scroll easily, so we just set the adapter
                    adapter = new CatAdapter(myFavs, apiService, API_KEY);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<FavouriteResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Error loading favourites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = (hour >= 5 && hour < 12) ? "Good Morning! ☀️" :
                (hour >= 12 && hour < 17) ? "Good Afternoon! 🐱" :
                        (hour >= 17 && hour < 21) ? "Good Evening! 🌙" : "Hello Night Owl! 🦉";
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(greeting);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Mode: Random Cats
            searchContainer.setVisibility(View.GONE);
            currentBreedId = null; // Reset to random
            loadCats(false);       // Load fresh random batch

        } else if (id == R.id.nav_search) {
            // Mode: Search Breeds
            searchContainer.setVisibility(View.VISIBLE);
            // We don't load immediately; we wait for the Spinner selection (or user interaction)
            // But we can clear the list to look cleaner
            if(adapter != null) adapter.updateData(new ArrayList<>());

        } else if (id == R.id.nav_favourites) {
            // Mode: Favourites
            loadFavourites();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}