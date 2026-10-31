package com.example.thecat_alogue

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
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
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CatViewModel by viewModels()
    private lateinit var adapter: CatAdapter
    private lateinit var networkObserver: NetworkObserver
    private var selectedBreed: Breed? = null

    private var textToSpeech: TextToSpeech? = null
    private var sensorManager: SensorManager? = null
    private var lastShakeTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Apply Window Insets for Edge-to-Edge ---
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout) { _, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            binding.recyclerView.setPadding(
                binding.recyclerView.paddingLeft,
                binding.recyclerView.paddingTop,
                binding.recyclerView.paddingRight,
                systemBars.bottom + 16
            )
            insets
        }

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

        // --- Text To Speech ---
        initTextToSpeech()

        // --- Accelerometer Shake Detector ---
        initShakeDetector()

        // --- RecyclerView & Adapter ---
        adapter = CatAdapter(
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
            viewModel.loadCats(isPagination = false)
        }

        // Retry Button Listener
        binding.btnRetry.setOnClickListener {
            binding.emptyStateContainer.visibility = View.GONE
            viewModel.loadCats(isPagination = false)
        }

        // Cat Fact Refresh Button
        binding.btnRefreshFact.setOnClickListener { viewModel.loadCatFact() }

        // Cat Fact Speak Button
        binding.btnSpeakFact.setOnClickListener {
            val fact = binding.txtCatFact.text.toString()
            if (fact.isNotEmpty()) {
                speakFact(fact)
            }
        }

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

        // --- Handle Shortcut Intent ---
        handleShortcutIntent(intent)

        // --- Initial Load ---
        if (savedInstanceState == null) {
            viewModel.loadBreeds()
            viewModel.loadCats(isPagination = false)
            viewModel.loadCatFact()
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    private var isTtsInitialized = false

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                isTtsInitialized = true
            } else {
                isTtsInitialized = false
            }
        }
    }

    private fun speakFact(factText: String) {
        if (!isTtsInitialized || textToSpeech == null) {
            Toast.makeText(this, "Speech engine initializing, tap again in a moment...", Toast.LENGTH_SHORT).show()
            return
        }
        textToSpeech?.speak(factText, TextToSpeech.QUEUE_FLUSH, null, "CAT_FACT_TTS")
    }

    private fun initShakeDetector() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer != null) {
            sensorManager?.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    val gX = x / SensorManager.GRAVITY_EARTH
                    val gY = y / SensorManager.GRAVITY_EARTH
                    val gZ = z / SensorManager.GRAVITY_EARTH
                    val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

                    if (gForce > 2.7f) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastShakeTime > 2000) {
                            lastShakeTime = currentTime
                            binding.root.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            Toast.makeText(this@MainActivity, "Shaken! Fetching fresh felines...", Toast.LENGTH_SHORT).show()
                            viewModel.loadCats(isPagination = false)
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun handleShortcutIntent(intent: Intent?) {
        val action = intent?.getStringExtra("shortcut_action")
        if (action == "search") {
            binding.searchContainer.visibility = View.VISIBLE
        } else if (action == "random") {
            binding.searchContainer.visibility = View.GONE
            viewModel.currentBreedId = null
            viewModel.loadCats(isPagination = false)
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
                            setupBreedAutoComplete(resource.data)
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
            }
        }
    }

    private fun setupBreedAutoComplete(breeds: List<Breed>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_dropdown_item,
            breeds
        )
        binding.breedAutoComplete.setAdapter(adapter)

        binding.breedAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            selectedBreed = parent.getItemAtPosition(position) as? Breed
            if (selectedBreed != null) {
                viewModel.currentBreedId = selectedBreed?.id
                viewModel.loadCats(isPagination = false)
            }
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

        setupPinchToZoom(dialogBinding.previewCatImage)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cat_detail_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.close) { d, _ -> d.dismiss() }
            .create()

        dialogBinding.btnPreviewDownload.setOnClickListener {
            CatUtils.downloadImage(this, cat.url, cat.id)
        }

        dialogBinding.btnPreviewBookmark.setOnClickListener {
            viewModel.addBookmark(cat)
            dialog.dismiss()
        }

        dialogBinding.btnPreviewShare.setOnClickListener {
            CatUtils.shareImageWithFileProvider(this, cat.url)
        }

        dialog.show()
    }

    private fun setupPinchToZoom(imageView: ImageView) {
        var scaleFactor = 1.0f
        val scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(1.0f, 4.0f)
                imageView.scaleX = scaleFactor
                imageView.scaleY = scaleFactor
                return true
            }
        })

        imageView.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            true
        }
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
                val breed = selectedBreed
                if (breed != null) {
                    viewModel.currentBreedId = breed.id
                    viewModel.loadCats(isPagination = false)
                }
            }
            R.id.nav_bookmarks -> {
                binding.searchContainer.visibility = View.GONE
                viewModel.loadBookmarks()
            }
            R.id.nav_share_app -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message))
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)))
            }
            R.id.nav_about -> {
                showAboutDialog()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
