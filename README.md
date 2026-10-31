# The Cat-alogue

<p align="center">
  <img src="app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp" width="120" alt="The Cat-alogue Logo" />
</p>

<p align="center">
  <b>A 100% Kotlin, production-grade Android application for feline enthusiasts.</b><br>
  Explore endless streams of cats, search by breed, save favourites offline, download cat pics & GIFs, and share the joy!
</p>

<p align="center">
  <a href="https://github.com/ShashankS1011/The-Cat-alogue/actions/workflows/android.yml"><img src="https://github.com/ShashankS1011/The-Cat-alogue/actions/workflows/android.yml/badge.svg" alt="Android CI Status" /></a>
  <img src="https://img.shields.io/badge/Language-Kotlin_100%25-blueviolet.svg" alt="Language: 100% Kotlin" />
  <img src="https://img.shields.io/badge/Architecture-MVVM_%2B_StateFlow-brightgreen.svg" alt="Architecture: MVVM + StateFlow" />
  <img src="https://img.shields.io/badge/minSdk-24-blue.svg" alt="minSdk: 24 (Android 7.0+)" />
  <img src="https://img.shields.io/badge/targetSdk-35-purple.svg" alt="targetSdk: 35 (Android 15)" />
  <a href="https://github.com/ShashankS1011/The-Cat-alogue/releases"><img src="https://img.shields.io/github/v/release/ShashankS1011/The-Cat-alogue?include_prereleases&label=Download%20APK&color=orange" alt="Download Release APK" /></a>
</p>

---

## Features

- **Endless Feline Stream**: Infinite scroll grid powered by [The Cat API](https://thecatapi.com/).
- **Cat Fact of the Day**: Live daily trivia and cat facts powered by [Cat Facts API](https://catfact.ninja/). Tap refresh for instant fun facts!
- **Breed Search & Detailed Info**: Filter images by breed (Bengal, Siamese, Persian, Maine Coon, etc.) and view comprehensive breed details in a Material BottomSheet.
- **Image & GIF Downloads**: One-tap direct download for images (`.jpg`, `.png`, `.webp`) and animated GIFs (`.gif`) to the public Downloads folder via Android `DownloadManager`.
- **Rich Media & URI Sharing**: Share cat URLs cleanly across messaging and social media apps.
- **Offline Persistence & Offline Feed Caching**: Full offline support powered by Room Database (`androidx.room`). Cats and breeds are automatically cached locally so users can browse previously fetched feeds without internet.
- **Real-Time Network Observer**: Live network connectivity callback displaying a subtle offline status banner when connection is lost.
- **Haptic Feedback & Animations**: Tactile haptic feedback and smooth heart bounce animations when interacting with favourite and download buttons.
- **Clear All Favourites**: One-tap action to clear saved favourites with a Material 3 confirmation dialog.
- **Golden Dark & Material 3 Themes**: Warm, cat-inspired color palette designed for Material 3 with high contrast for Dark and Light modes.
- **Android 12+ Splash Screen**: Smooth application launch experience powered by `androidx.core:core-splashscreen`.

---

## Architecture & Tech Stack

**The Cat-alogue** follows **Clean Architecture**, **MVVM**, and Google's recommended **Android Architecture Components**, written entirely in **100% Idiomatic Kotlin**:

```
com.example.thecat_alogue
├── MainActivity.kt               # Main Activity, Drawer Navigation, StateFlow Collection
├── SplashActivity.kt             # Animated Launch Splash Screen
├── CatViewModel.kt               # MVVM ViewModel managing StateFlow UI states & coroutine scopes
├── CatRepository.kt              # Single Source of Truth handling Retrofit API & Room DB
├── AppDatabase.kt                # Room Database v2 with KTX coroutines
├── FavoriteCatDao.kt             # Room DAO for offline favourites
├── BreedDao.kt                   # Room DAO for offline breed caching
├── CachedCatDao.kt               # Room DAO for offline image feed caching
├── CatAdapter.kt                 # RecyclerView ListAdapter with DiffUtil & Haptic Feedback
├── CatApiService.kt              # Retrofit REST API interface with suspend functions
├── NetworkObserver.kt            # Real-time ConnectivityManager NetworkCallback observer
├── CatUtils.kt                   # Kotlin helper object for downloads and greeting utilities
└── models/                       # Type-safe Kotlin data classes
    ├── CatImage.kt
    ├── Breed.kt
    ├── CatFact.kt
    ├── FavouriteRequest.kt
    └── FavouriteResponse.kt
```

### Modern Android Stack
- **Language**: 100% Idiomatic Kotlin (Coroutines, StateFlow, SharedFlow, Sealed Data Classes, Extension Functions)
- **Architecture**: MVVM + Repository Pattern + Clean Architecture
- **Concurrency & Reactive Data**: Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`) + `StateFlow` & `SharedFlow`
- **UI & Components**: Android ViewBinding, Material Components 3, Navigation Drawer, BottomSheetDialog, SwipeRefreshLayout, RecyclerView (`ListAdapter` + `DiffUtil`)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) with Gson Converter & `suspend` functions
- **Image Loading**: [Glide 4](https://github.com/bumptech/glide) with crossfade transitions and progress spinners
- **Local Database**: [Room KTX](https://developer.android.com/training/data-storage/room) with Coroutines Flow
- **Testing**: JUnit4, Mockito, Kotlin Coroutines Test, Room In-Memory Database Tests

---

## Testing

The app includes unit tests for data models, utility functions, state logic, and Room DAO in-memory database tests:

```bash
# Run Unit Tests
./gradlew testDebugUnitTest

# Run Android Lint
./gradlew lintDebug
```

---

## Security & Policy Compliance

- **API Key Protection**: Secrets managed via `local.properties` and injected into `BuildConfig.CAT_API_KEY` at build time.
- **Strict Network Security**: Enforces HTTPS (`android:usesCleartextTraffic="false"`).
- **Secure File Provider**: Configured `FileProvider` paths for secure URI generation.
- **Automated Dependency Updates**: Managed via Dependabot ([.github/dependabot.yml](.github/dependabot.yml)).

---

## About The Developer

> *"Hi there, I'm Shashank (aka TheSusHero)! Fun fact: I don't actually own any cats (yet!), but I'm an absolute fan of both cats and dogs! I built The Cat-alogue to share the joy of feline friends with everyone. Code powered by 90% caffeine, 10% pet memes, and 0% scratches!"*
> 
> — **Shashank** ([@ShashankS1011](https://github.com/ShashankS1011))

---

## Building & Running Locally

### Prerequisites
- **Android Studio**: Jellyfish (2023.3.1) or Ladybug (2024.2.1+)
- **JDK**: Java 17+
- **Android SDK**: `compileSdk = 35`, `minSdk = 24`

### Quick Start
```bash
# 1. Clone the repository
git clone https://github.com/ShashankS1011/The-Cat-alogue.git
cd The-Cat-alogue

# 2. Configure local.properties
echo "THE_CAT_API_KEY=live_pAAbxB0S79QEhi9f122NsmHhuo7PgncwMptCEPDi9yTdubmufGFTitiXsKlrQbik" >> local.properties

# 3. Build & test
./gradlew assembleDebug testDebugUnitTest
```

---

## License

Distributed under the MIT License. See `LICENSE` for details.

<p align="center">Made by <a href="https://github.com/ShashankS1011">Shashank (TheSusHero)</a></p>
