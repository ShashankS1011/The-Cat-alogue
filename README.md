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
</p>

---

## Download Latest Release

Want to install **The Cat-alogue** directly on your Android phone or tablet? Download the latest pre-built APK directly from GitHub Releases:

<p align="center">
  <a href="https://github.com/ShashankS1011/The-Cat-alogue/releases/latest">
    <img src="https://img.shields.io/badge/Download-Release_APK_(v1.0.8)-26A69A?style=for-the-badge&logo=android&logoColor=white&labelColor=121212" alt="Download Release APK" />
  </a>
</p>

- **Requirements**: Android 7.0+ (API Level 24+)
- **Latest Release**: [v1.0.8 on GitHub Releases](https://github.com/ShashankS1011/The-Cat-alogue/releases/latest)

---

## Features

- **Endless Feline Stream**: Infinite scroll grid powered by [The Cat API](https://thecatapi.com/).
- **Cat Fact of the Day**: Live daily trivia and cat facts powered by [Cat Facts API](https://catfact.ninja/). Tap refresh for instant fun facts!
- **Text-to-Speech (TTS)**: Tap the speaker button to hear daily cat facts read aloud natively.
- **Breed Search & Detailed Info**: Instant predictive search across 60+ breeds with comprehensive breed details in a Material BottomSheet.
- **Image & GIF Downloads**: One-tap direct download for images (`.jpg`, `.png`, `.webp`) and animated GIFs (`.gif`) to the public Downloads folder via Android `DownloadManager`.
- **Direct Binary Image Sharing**: Share actual image files securely across messaging and social media apps.
- **Offline Persistence & Feed Caching**: Full offline support powered by Room Database (`androidx.room`). Cats and breeds are automatically cached locally.
- **Real-Time Network Observer**: Live network connectivity callback displaying a subtle offline status banner when connection is lost.
- **Gesture Controls**: Pinch-to-zoom on preview images and shake-to-refresh random feeds with tactile haptic feedback.
- **Material You Dynamic Colors**: Material 3 theme adapting to user system wallpaper on Android 12+.

---

## Architecture & Tech Stack

**The Cat-alogue** follows **Clean Architecture**, **MVVM**, and Google's recommended **Android Architecture Components**, written entirely in **100% Idiomatic Kotlin**:

```
com.example.thecat_alogue
├── MainActivity.kt               # Main Activity, Drawer Navigation, StateFlow Collection
├── SplashActivity.kt             # Animated Launch Splash Screen
├── CatViewModel.kt               # MVVM ViewModel managing StateFlow UI states & coroutines
├── CatRepository.kt              # Single Source of Truth handling Retrofit API & Room DB
├── AppDatabase.kt                # Room Database v4 with KTX coroutines
├── BookmarkDao.kt                # Room DAO for local collections & bookmarks
├── BreedDao.kt                   # Room DAO for offline breed caching
├── CachedCatDao.kt               # Room DAO for offline image feed caching
├── CatAdapter.kt                 # RecyclerView ListAdapter with DiffUtil & Haptic Feedback
├── CatApiService.kt              # Retrofit REST API interface with suspend functions
├── NetworkObserver.kt            # Real-time ConnectivityManager NetworkCallback observer
├── CatUtils.kt                   # Kotlin helper object for downloads and sharing
└── models/                       # Type-safe Kotlin data classes
    ├── CatImage.kt
    ├── Breed.kt
    ├── CatFact.kt
    └── BookmarkEntity.kt
```

### Modern Android Stack
- **Language**: 100% Idiomatic Kotlin (Coroutines, StateFlow, SharedFlow, Extension Functions)
- **Architecture**: MVVM + Repository Pattern + Clean Architecture
- **Concurrency**: Kotlin Coroutines (`viewModelScope`, `Dispatchers.IO`) + `StateFlow` & `SharedFlow`
- **UI & Components**: Android ViewBinding, Material Components 3, Dynamic Colors, Navigation Drawer, BottomSheetDialog, SwipeRefreshLayout, RecyclerView (`ListAdapter` + `DiffUtil`)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) with Gson Converter & `suspend` functions
- **Image Loading**: [Glide 4](https://github.com/bumptech/glide) with crossfade transitions and progress spinners
- **Local Database**: [Room KTX](https://developer.android.com/training/data-storage/room) with KSP annotation processing
- **Testing**: JUnit4, Mockito, Kotlin Coroutines Test

---

## Testing

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
./gradlew assembleRelease testDebugUnitTest
```

---

## License

Distributed under the MIT License. See `LICENSE` for details.

<p align="center">Made by <a href="https://github.com/ShashankS1011">Shashank</a></p>
