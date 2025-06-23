# Smart Kitchen Scale – Android App

This is an Android application university project for managing meals using a custom smart kitchen scale.

## Features

- **Meal diary** – browse meals by date with calorie and macronutrient totals.
- **Add meals** – pick date/time, emoji and ingredients. Search products online or add custom ones.
- **UDP scale integration** – a foreground service listens on port `8000` for barcode and weight data, fetches product info from OpenFoodFacts and saves meals locally.
- **Local persistence** – Room database stores meals and ingredients, mapped to domain models by `MealsLocalRepository`.
- **Custom UI** – animated progress bars and other Material3 components.

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/7a7b6d61-bdd9-4b70-9dc2-df2dd5978c1a" width="200" />
  <img src="https://github.com/user-attachments/assets/2de044f8-998f-4490-8612-e8b7f3e6d09b" width="200" />
  <img src="https://github.com/user-attachments/assets/d7e45596-2c0f-46a7-aebc-26afed1fa945" width="200" />
</p>

## Libraries Used

- AndroidX Navigation, Room, ViewModel
l, LiveData
- Retrofit with Gson converter
- Kotlin Coroutines
- Material3 UI components
- Glide for image loading

## Repository Structure

```
app/
 ├── src/main/java/com/example/smartscale/
 │   ├── core            # dialogs, custom views and utilities
 │   ├── data            # Room database and Retrofit client
 │   ├── domain          # domain models and repository
 │   └── ui              # fragments and viewmodels
 └── src/main/res/       # layouts, navigation graph, strings
```

Built as a learning project demonstrating Android practices with a simple scale integration.
