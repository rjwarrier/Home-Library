# Home Library

Home Library is a Kotlin and Jetpack Compose Android app for managing a personal book collection, physical shelf locations, ISBN metadata lookup, loans, reminders, and local backup/export.

The Android package and application id are:

```text
com.mj.homelibrary
```

## Build

```powershell
.\gradlew.bat :app:assembleDebug
```

The project keeps user-visible text in Android resources so the app can be localized by adding locale-specific `res/values-*` folders.
