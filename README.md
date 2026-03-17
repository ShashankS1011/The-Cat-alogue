# 🐾 Android Cat Explorer

> A modern, native Android application that lets users explore the world of cats. 


## 📖 About This Project

Built to demonstrate modern Android development practices, **Cat Explorer** is a feature-rich application that interfaces with a public REST API to deliver an endless stream of feline content. 

This project serves as a practical implementation of **Clean Architecture** and the **MVVM** pattern, ensuring separation of concerns, testability, and a highly scalable codebase.

## ✨ Key Features

* **Infinite Scroll:** Seamlessly browse a never-ending, paginated feed of random cats.
* **Smart Breed Search:** Quickly filter and discover specific cats using an intuitive dropdown search interface.
* **Save & Favorite:** Keep track of your favorite feline friends with local persistence.
* **Share the Joy:** Native Android sharing capabilities to send your favorite images to friends or other apps.
* **Robust Error Handling:** Graceful UI states for loading, network errors, and empty results.

## 🏗️ Architecture & Tech Stack

This app leverages modern Android development standards:

* **Language:** [Kotlin](https://kotlinlang.org/)
* **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
* **Networking:** [Retrofit2](https://square.github.io/retrofit/) & OkHttp for REST API integration
* **Asynchrony:** Kotlin Coroutines & Flow
* **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) / Dagger *(Update this if you used Koin or manual DI)*
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/) / Glide *(Update based on your choice)*
* **Local Storage:** Room Database *(If applicable for the "Save" feature)*
