# FindDining

An Android app for browsing restaurants, viewing details, photos, reviews, and seeing each restaurant on a map.

## Setup
- Android Studio with SDK 24+ and JDK 17.
- Google Maps SDK for Android enabled in your Google Cloud project.
- Add your Maps key:
  - In `local.properties` (preferred, not checked in):  
    `MAPS_API_KEY=YOUR_KEY`
  - Or export `MAPS_API_KEY` in your shell before building.

## Build & Run
- `./gradlew assembleDebug` or run from Android Studio.
- On first launch, grant location permission for the My Location layer.

