# CurrencyConvertor

![module-graph](https://github.com/user-attachments/assets/8ce5d5ee-3220-49d2-a0c8-55ad30a30c1d)

This project is a multi-module Android application that leverages Jetpack Compose for UI, Datastore for local storage, Room for database management, and Ktor for network requests. The project is organized with a Gradle multi-module structure, following the MVVM (Model-View-ViewModel) architecture and utilizing Kotlin Coroutines for asynchronous operations. Comprehensive unit and instrumentation tests are implemented to ensure the stability and reliability of the app.

## Project Structure

The project is organized into the following modules:

- **:app** - The main application module that ties everything together and contains the entry point of the application.
- **:feature** - Contains individual features like `conversion` with specific functionality and UI.
- **:core** - Core modules with reusable components, such as:
  - **designsystem** - Reusable UI components and themes, leveraging Jetpack Compose.
  - **data** - Manages data sources and repository implementations, coordinating between local and network data.
  - **database** - Defines the Room database and entities for persistent local storage.
  - **network** - Contains network configurations and API calls, using Ktor client.
  - **datastore** - Implements Datastore for key-value data storage.
  - **common** - Contains utility classes and shared resources used across modules.
  - **model** - Defines data models shared across different modules.
- **:sync** - Responsible for background tasks and scheduling, including a **work** module for managing periodic or one-time tasks using WorkManager.

## Key Technologies

- **Jetpack Compose** - For building the UI declaratively.
- **Gradle Multi-Modules** - Structuring the project for better code organization, modularity, and reusability.
- **Datastore** - Modern key-value storage solution for handling user preferences.
- **Room** - Database library for handling structured data persistence.
- **Ktor Client** - For making HTTP network requests.
- **Kotlin Coroutines** - Simplifies asynchronous programming, providing a smoother experience for handling background tasks.
- **MVVM Architecture** - Separates UI, data, and logic, making the application easier to test and maintain.
- **Testing** - Includes unit tests and instrumentation tests to ensure code correctness and reliability.

## Getting Started

### Prerequisites

- Android Studio Dolphin or later
- Kotlin 1.7.0 or later
- Gradle 7.0 or later

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/esskay09/CurrencyConvertor.git

