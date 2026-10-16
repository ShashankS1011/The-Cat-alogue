package com.example.thecat_alogue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecat_alogue.databinding.ActivityMainBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private CatViewModel viewModel;
    private CatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CatViewModel.class);

        // --- Toolbar & Greeting ---
        setSupportActionBar(binding.toolbar);
        setupGreeting();

        // --- Navigation Drawer ---
        binding.navView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // --- RecyclerView & Adapter ---
        adapter = new CatAdapter(cat -> viewModel.addFavourite(cat.getId()));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);

        // Infinite Scroll Listener
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadCats(true);
                }
            }
        });

        // Pull to Refresh Listener
        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadCats(false));

        // --- Credits Click Listener ---
        binding.txtCredits.setOnClickListener(v -> showAboutDialog());

        // --- Back Button Handler ---
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // --- Observe ViewModel States ---
        observeViewModel();

        // --- Initial Load ---
        if (savedInstanceState == null) {
            viewModel.loadBreeds();
            viewModel.loadCats(false);
        }
    }

    private void observeViewModel() {
        viewModel.getCatsLiveData().observe(this, resource -> {
            if (resource == null) return;

            if (resource.isLoading()) {
                binding.swipeRefreshLayout.setRefreshing(true);
            } else {
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            if (resource.isSuccess() && resource.getData() != null) {
                adapter.submitList(new ArrayList<>(resource.getData()));
            } else if (resource.isError()) {
                Toast.makeText(this, resource.getMessage() != null ? resource.getMessage() : getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getBreedsLiveData().observe(this, resource -> {
            if (resource != null && resource.isSuccess() && resource.getData() != null) {
                setupBreedSpinner(resource.getData());
            }
        });

        viewModel.getToastMessageLiveData().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBreedSpinner(List<Breed> breeds) {
        ArrayAdapter<Breed> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                breeds
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.breedSpinner.setAdapter(spinnerAdapter);

        binding.breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Breed selected = (Breed) parent.getItemAtPosition(position);
                if (binding.searchContainer.getVisibility() == View.VISIBLE && selected != null) {
                    viewModel.setCurrentBreedId(selected.getId());
                    viewModel.loadCats(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = (hour >= 5 && hour < 12) ? getString(R.string.good_morning) :
                (hour >= 12 && hour < 17) ? getString(R.string.good_afternoon) :
                        (hour >= 17 && hour < 21) ? getString(R.string.good_evening) : getString(R.string.hello_night_owl);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(greeting);
        }
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about_dialog_title)
                .setMessage(R.string.about_dialog_funny)
                .setPositiveButton(R.string.about_github_button, (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ShashankS1011"));
                    startActivity(browserIntent);
                })
                .setNegativeButton(R.string.close, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            binding.searchContainer.setVisibility(View.GONE);
            viewModel.setCurrentBreedId(null);
            viewModel.loadCats(false);
        } else if (id == R.id.nav_search) {
            binding.searchContainer.setVisibility(View.VISIBLE);
            adapter.submitList(new ArrayList<>());
        } else if (id == R.id.nav_favourites) {
            binding.searchContainer.setVisibility(View.GONE);
            viewModel.loadFavourites();
        } else if (id == R.id.nav_about) {
            showAboutDialog();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
