# 🐾 Android Cat Explorer

> A modern, native Android application that lets users explore the world of cats. 


## 📖 About This Project

Built to demonstrate clean architecture and REST API integration, **Cat Explorer** is a native Android application that delivers an endless stream of feline content. 

This project was developed from the ground up using Java and XML to showcase a strong understanding of Android's core view system, robust network handling, and structured software design patterns.

## ✨ Key Features

* **Infinite Scroll:** Seamlessly browse a never-ending, paginated feed of random cats.
* **Smart Breed Search:** Quickly filter and discover specific cats using an intuitive dropdown search interface.
* **Save & Favorite:** Keep track of your favorite feline friends using local persistence.
* **Share the Joy:** Native Android sharing capabilities to send your favorite images to friends or other apps.
* **Robust Error Handling:** Graceful UI states for loading, network errors, and empty results.

## 🏗️ Architecture & Tech Stack

This application is built with a focus on maintainability and separation of concerns:

* **Language:** Java
* **UI Design:** XML Layouts & Material Design Components
* **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
* **Networking:** Retrofit2 & OkHttp for efficient REST API consumption
* **Image Loading:** Glide (for smooth, cached image rendering)
* **Local Storage:** Room Database (for saving favorite cats offline)
* **Asynchrony:** Java Executors / RxJava / LiveData (for background threading and UI updates)
