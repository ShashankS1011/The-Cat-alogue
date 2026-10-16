package com.example.thecat_alogue;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.bumptech.glide.Glide;
import com.example.thecat_alogue.databinding.ActivityMainBinding;
import com.example.thecat_alogue.databinding.BottomSheetBreedInfoBinding;
import com.example.thecat_alogue.databinding.DialogCatPreviewBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private CatViewModel viewModel;
    private CatAdapter adapter;
    private Breed selectedBreed = null;

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
        adapter = new CatAdapter(
                cat -> viewModel.addFavourite(cat),
                this::showCatPreviewDialog
        );
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

        // Retry Button Listener
        binding.btnRetry.setOnClickListener(v -> {
            binding.emptyStateContainer.setVisibility(View.GONE);
            viewModel.loadCats(false);
        });

        // Breed Info Button Listener
        binding.btnBreedInfo.setOnClickListener(v -> {
            if (selectedBreed != null) {
                showBreedBottomSheet(selectedBreed);
            } else {
                Toast.makeText(this, "Select a breed first!", Toast.LENGTH_SHORT).show();
            }
        });

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
                if (adapter.getItemCount() == 0) {
                    binding.loadingContainer.setVisibility(View.VISIBLE);
                }
                binding.swipeRefreshLayout.setRefreshing(true);
            } else {
                binding.loadingContainer.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            if (resource.isSuccess() && resource.getData() != null) {
                List<CatImage> catList = resource.getData();
                if (catList.isEmpty()) {
                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyStateContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    adapter.submitList(new ArrayList<>(catList));
                }
            } else if (resource.isError()) {
                binding.loadingContainer.setVisibility(View.GONE);
                if (adapter.getItemCount() == 0) {
                    binding.emptyStateContainer.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
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
                selectedBreed = (Breed) parent.getItemAtPosition(position);
                if (binding.searchContainer.getVisibility() == View.VISIBLE && selectedBreed != null) {
                    viewModel.setCurrentBreedId(selectedBreed.getId());
                    viewModel.loadCats(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupGreeting() {
        String greeting = getString(CatUtils.getTimeGreeting());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(greeting);
            getSupportActionBar().setSubtitle("Your Feline Haven");
        }
    }

    private void showBreedBottomSheet(Breed breed) {
        BottomSheetBreedInfoBinding sheetBinding = BottomSheetBreedInfoBinding.inflate(LayoutInflater.from(this));
        sheetBinding.txtBreedName.setText(breed.getName());
        sheetBinding.txtBreedOrigin.setText(getString(R.string.breed_origin,
                breed.getOrigin() != null ? breed.getOrigin() : "Unknown",
                breed.getLifeSpan() != null ? breed.getLifeSpan() : "12 - 15"));
        sheetBinding.txtBreedTemperament.setText(breed.getTemperament() != null ? breed.getTemperament() : "");
        sheetBinding.txtBreedDescription.setText(breed.getDescription() != null ? breed.getDescription() : "");

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(sheetBinding.getRoot());

        if (breed.getWikipediaUrl() != null && !breed.getWikipediaUrl().isEmpty()) {
            sheetBinding.btnWiki.setVisibility(View.VISIBLE);
            sheetBinding.btnWiki.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(breed.getWikipediaUrl()));
                startActivity(browserIntent);
            });
        } else {
            sheetBinding.btnWiki.setVisibility(View.GONE);
        }

        dialog.show();
    }

    private void showCatPreviewDialog(CatImage cat) {
        DialogCatPreviewBinding dialogBinding = DialogCatPreviewBinding.inflate(LayoutInflater.from(this));

        Glide.with(this)
                .load(cat.getUrl())
                .into(dialogBinding.previewCatImage);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.cat_detail_title)
                .setView(dialogBinding.getRoot())
                .setNegativeButton(R.string.close, (d, which) -> d.dismiss())
                .create();

        dialogBinding.btnPreviewShare.setOnClickListener(v -> {
            String message = getString(R.string.share_cat_message, cat.getUrl());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        });

        dialogBinding.btnPreviewFavorite.setOnClickListener(v -> {
            viewModel.addFavourite(cat);
            dialog.dismiss();
        });

        dialog.show();
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
