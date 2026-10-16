# The Cat-alogue

<p align="center">
  <img src="app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp" width="120" alt="The Cat-alogue App Logo" />
</p>

<p align="center">
  <b>A modern, production-grade Android application for feline and pet enthusiasts.</b><br>
  Explore endless streams of cats, search by breed, save favorites, and share the joy!
</p>

<p align="center">
  <a href="https://github.com/ShashankS1011/The-Cat-alogue/actions/workflows/android.yml"><img src="https://github.com/ShashankS1011/The-Cat-alogue/actions/workflows/android.yml/badge.svg" alt="Android CI Status" /></a>
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/minSdk-24-blue.svg" alt="minSdk: 24 (Android 7.0+)" />
  <img src="https://img.shields.io/badge/targetSdk-35-purple.svg" alt="targetSdk: 35 (Android 15)" />
  <a href="https://github.com/ShashankS1011/The-Cat-alogue/releases"><img src="https://img.shields.io/github/v/release/ShashankS1011/The-Cat-alogue?include_prereleases&label=Download%20APK&color=orange" alt="Download Release APK" /></a>
</p>

---

## Download APK

Want to try the app on your phone? Download the latest pre-built APK directly from GitHub Releases:

* **[Download Latest APK from GitHub Releases](https://github.com/ShashankS1011/The-Cat-alogue/releases/latest)**

---

## Key Features

- **Infinite Scroll Feed**: Infinite pagination feed powered by [The Cat API](https://thecatapi.com/). Never run out of cats!
- **Cat Fact of the Day**: Live daily cat facts and trivia card powered by [Cat Facts API](https://catfact.ninja/). Tap refresh for instant fun facts!
- **Breed Search & Details**: Filter images by specific cat breeds (Bengal, Siamese, Persian, Maine Coon, and more) using a dynamic search dropdown and view full breed details in a Material BottomSheet.
- **Interactive Image Preview**: Tap any cat card in the grid to launch an enlarged preview dialog with quick-action Share and Favorite options.
- **Offline Storage**: Save your favorite cats locally with Room Database (`androidx.room`) and view them anytime, even offline!
- **Secure File Sharing**: Share image links or content URIs securely using Android `FileProvider`.
- **Dedicated Loading Screen**: Centered loading layout ("Fetching feline goodness...") when fetching initial data or switching views.
- **Golden Dark & M3 Themes**: High-contrast, beautiful Material 3 color roles designed for Dark and Light modes.
- **Android 12+ Splash Screen**: 3-second animated application launch experience powered by `androidx.core:core-splashscreen`.
- **Funny "About Developer" Modal**: Interactive dialog revealing the developer's story (huge fan of both cats & dogs!), pet meme trivia, and GitHub repository link.

---

## Architecture & Tech Stack

**The Cat-alogue** is built adhering to strict modern Android production standards using pure **Java, Kotlin, and XML**:

```
com.example.thecat_alogue
├── MainActivity.java            # UI Host, Navigation Drawer, LiveData Observer
├── CatViewModel.java            # MVVM ViewModel retaining state across rotation
├── CatRepository.java           # Single source of truth for network API & Room DB
├── AppDatabase.java             # Room SQLite Database Instance
├── FavoriteCatDao.java          # Room Data Access Object
├── Resource.java                # Generic state wrapper (LOADING, SUCCESS, ERROR)
├── CatAdapter.java               # ListAdapter with DiffUtil for smooth animations
├── CatApiService.java           # Retrofit REST API Interface
├── CatUtils.kt                  # Kotlin Helper object for Java-Kotlin interop
└── models/                      # Clean POJO data models
```

### Libraries & Tools
- **Architecture**: Clean Architecture + MVVM + Repository Pattern + Room Database
- **UI & Views**: Android ViewBinding, Material Components 3, Navigation Drawer, BottomSheetDialog, SwipeRefreshLayout, RecyclerView (`ListAdapter` + `DiffUtil`)
- **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + Gson Converter + Cat Facts API
- **Image Loading**: [Glide 4](https://github.com/bumptech/glide) with crossfade transitions and progress indicators
- **Async & Lifecycle**: `androidx.lifecycle` (`ViewModel` + `LiveData`), `OnBackPressedDispatcher`, Executors
- **Testing**: JUnit4 & Mockito unit test suite
- **CI/CD**: GitHub Actions pipeline for building, linting, unit testing, and automated release generation

---

## Security & Policy Compliance

This repository enforces strict security practices:
- **API Key Masking**: No hardcoded secrets in source code. Secrets are managed via `local.properties` and injected dynamically into `BuildConfig.CAT_API_KEY`.
- **HTTPS Enforcement**: `android:usesCleartextTraffic="false"` explicitly configured in [AndroidManifest.xml](app/src/main/AndroidManifest.xml).
- **FileProvider Security**: Secure content URI generation for file sharing via `FileProvider`.
- **Security Policy**: See [SECURITY.md](SECURITY.md) for vulnerability reporting guidelines and private vulnerability reporting details.
- **Dependabot**: Automated dependency and GitHub Actions updates configured in [.github/dependabot.yml](.github/dependabot.yml).
- **Branch Protection**: `main` branch protected on GitHub with enforced status checks.

---

## About The Developer

> *"Fun fact: I don't actually own any cats (yet!), but I'm an absolute fan of both cats and dogs! I built The Cat-alogue to share the joy of feline friends with everyone. Code powered by 90% caffeine, 10% pet memes, and 0% scratches!"*
> 
> — **Shashank** ([@ShashankS1011](https://github.com/ShashankS1011))

---

## How to Build & Run Locally

### Prerequisites
- **Android Studio**: Jellyfish (2023.3.1) or newer
- **JDK**: Java 17+
- **Android SDK**: `compileSdk = 35`, `minSdk = 24`

### Steps
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/ShashankS1011/The-Cat-alogue.git
   cd The-Cat-alogue
   ```

2. **Configure API Key**:
   Create or update `local.properties` in the project root folder:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   THE_CAT_API_KEY=live_pAAbxB0S79QEhi9f122NsmHhuo7PgncwMptCEPDi9yTdubmufGFTitiXsKlrQbik
   ```

3. **Build & Test via Gradle**:
   ```bash
   # Run Unit Tests
   ./gradlew testDebugUnitTest

   # Run Android Lint
   ./gradlew lintDebug

   # Assemble Debug APK
   ./gradlew assembleDebug
   ```

4. **Install on Device**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

---

## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="center">Made by <a href="https://github.com/ShashankS1011">Shashank (TheSusHero)</a></p>
