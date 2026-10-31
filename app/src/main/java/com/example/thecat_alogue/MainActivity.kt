package com.example.thecat_alogue

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.thecat_alogue.databinding.ActivityMainBinding
import com.example.thecat_alogue.databinding.BottomSheetBreedInfoBinding
import com.example.thecat_alogue.databinding.DialogCatPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()
    private lateinit var adapter: CatAdapter
    private lateinit var networkObserver: NetworkObserver
    private var selectedBreed: Breed? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Toolbar & Greeting ---
        setSupportActionBar(binding.toolbar)
        setupGreeting()

        // --- Navigation Drawer ---
        binding.navView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // --- RecyclerView & Adapter ---
        adapter = CatAdapter(
            favouriteClickListener = { cat -> viewModel.toggleFavourite(cat) },
            catClickListener = { cat -> showCatPreviewDialog(cat) },
            downloadClickListener = { cat -> CatUtils.downloadImage(this, cat.url, cat.id) }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.adapter = adapter

        // Infinite Scroll Listener
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadCats(isPagination = true)
                }
            }
        })

        // Pull to Refresh Listener
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (viewModel.isFavouritesMode.value) {
                viewModel.loadFavourites()
            } else {
                viewModel.loadCats(isPagination = false)
            }
        }

        // Retry Button Listener
        binding.btnRetry.setOnClickListener {
            binding.emptyStateContainer.visibility = View.GONE
            viewModel.loadCats(isPagination = false)
        }

        // Cat Fact Refresh Button
        binding.btnRefreshFact.setOnClickListener { viewModel.loadCatFact() }

        // Breed Info Button Listener
        binding.btnBreedInfo.setOnClickListener {
            val breed = selectedBreed
            if (breed != null) {
                showBreedBottomSheet(breed)
            } else {
                Toast.makeText(this, "Select a breed first!", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Back Button Handler ---
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // --- Observe ViewModel & Network ---
        observeViewModel()
        observeNetwork()

        // --- Initial Load ---
        if (savedInstanceState == null) {
            viewModel.loadBreeds()
            viewModel.loadCats(isPagination = false)
            viewModel.loadCatFact()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val clearFavsItem = menu.findItem(R.id.action_clear_favourites)
        clearFavsItem?.isVisible = viewModel.isFavouritesMode.value
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear_favourites -> {
                showConfirmClearFavouritesDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeNetwork() {
        networkObserver = NetworkObserver(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                networkObserver.observe().collectLatest { isConnected ->
                    viewModel.setOffline(!isConnected)
                    binding.offlineBanner.visibility = if (isConnected) View.GONE else View.VISIBLE
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.catsUiState.collectLatest { resource ->
                        if (resource.isLoading) {
                            if (adapter.itemCount == 0) {
                                binding.loadingContainer.visibility = View.VISIBLE
                            }
                            binding.swipeRefreshLayout.isRefreshing = true
                        } else {
                            binding.loadingContainer.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                        }

                        if (resource.isSuccess && resource.data != null) {
                            val list = resource.data
                            if (list.isEmpty()) {
                                binding.emptyStateContainer.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                            } else {
                                binding.emptyStateContainer.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                adapter.submitList(ArrayList(list))
                            }
                        } else if (resource.isError) {
                            binding.loadingContainer.visibility = View.GONE
                            if (adapter.itemCount == 0) {
                                binding.emptyStateContainer.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                            }
                            Toast.makeText(
                                this@MainActivity,
                                resource.message ?: getString(R.string.network_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                launch {
                    viewModel.breedsUiState.collectLatest { resource ->
                        if (resource.isSuccess && resource.data != null) {
                            setupBreedSpinner(resource.data)
                        }
                    }
                }

                launch {
                    viewModel.catFactState.collectLatest { fact ->
                        if (fact.isNotEmpty()) {
                            binding.txtCatFact.text = fact
                        }
                    }
                }

                launch {
                    viewModel.toastMessageEvent.collectLatest { message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                launch {
                    viewModel.isFavouritesMode.collectLatest {
                        invalidateOptionsMenu()
                    }
                }
            }
        }
    }

    private fun setupBreedSpinner(breeds: List<Breed>) {
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            breeds
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.breedSpinner.adapter = spinnerAdapter

        binding.breedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedBreed = parent?.getItemAtPosition(position) as? Breed
                if (binding.searchContainer.visibility == View.VISIBLE && selectedBreed != null) {
                    viewModel.currentBreedId = selectedBreed?.id
                    viewModel.loadCats(isPagination = false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupGreeting() {
        val greeting = getString(CatUtils.getTimeGreeting())
        supportActionBar?.apply {
            title = greeting
            subtitle = "Your Feline Haven"
        }
    }

    private fun showBreedBottomSheet(breed: Breed) {
        val sheetBinding = BottomSheetBreedInfoBinding.inflate(layoutInflater)
        sheetBinding.txtBreedName.text = breed.name
        sheetBinding.txtBreedOrigin.text = getString(
            R.string.breed_origin,
            breed.origin ?: "Unknown",
            breed.lifeSpan ?: "12 - 15"
        )
        sheetBinding.txtBreedTemperament.text = breed.temperament ?: ""
        sheetBinding.txtBreedDescription.text = breed.description ?: ""

        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheetBinding.root)
        }

        if (!breed.wikipediaUrl.isNullOrEmpty()) {
            sheetBinding.btnWiki.visibility = View.VISIBLE
            sheetBinding.btnWiki.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(breed.wikipediaUrl)))
            }
        } else {
            sheetBinding.btnWiki.visibility = View.GONE
        }

        dialog.show()
    }

    private fun showCatPreviewDialog(cat: CatImage) {
        val dialogBinding = DialogCatPreviewBinding.inflate(layoutInflater)

        Glide.with(this)
            .load(cat.url)
            .into(dialogBinding.previewCatImage)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cat_detail_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.close) { d, _ -> d.dismiss() }
            .create()

        dialogBinding.btnPreviewDownload.setOnClickListener {
            CatUtils.downloadImage(this, cat.url, cat.id)
        }

        dialogBinding.btnPreviewShare.setOnClickListener {
            CatUtils.shareImageWithFileProvider(this, cat.url)
        }

        dialogBinding.btnPreviewFavorite.setOnClickListener {
            viewModel.toggleFavourite(cat)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showConfirmClearFavouritesDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.clear_all_favourites)
            .setMessage(R.string.confirm_clear_favourites)
            .setPositiveButton(R.string.clear) { _, _ ->
                viewModel.clearAllFavourites()
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_dialog_title)
            .setMessage(R.string.about_dialog_funny)
            .setPositiveButton(R.string.about_github_button) { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ShashankS1011")))
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                binding.searchContainer.visibility = View.GONE
                viewModel.currentBreedId = null
                viewModel.loadCats(isPagination = false)
            }
            R.id.nav_search -> {
                binding.searchContainer.visibility = View.VISIBLE
                adapter.submitList(emptyList())
            }
            R.id.nav_favourites -> {
                binding.searchContainer.visibility = View.GONE
                viewModel.loadFavourites()
            }
            R.id.nav_about -> {
                showAboutDialog()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
