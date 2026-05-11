# Repository Guidelines

## Project Structure & Module Organization
This repository contains a single Android app module:

- `app/` — main Android application module.
- `app/src/main/java/com/example/studentorganizer/` — Kotlin source code (UI, navigation, data/storage).
- `app/src/main/res/` — Android resources (`values/`, `drawable/`, `mipmap-*`, `xml/`).
- `app/src/test/` — local JVM unit tests.
- `app/src/androidTest/` — instrumented/device tests.
- `gradle/`, `gradlew`, `gradlew.bat` — Gradle wrapper and build tooling.

Keep new features grouped by responsibility (for example, new screens under `ui/screens/` and related state in dedicated ViewModels).

## Build, Test, and Development Commands
Run commands from repository root:

- `./gradlew assembleDebug` — builds a debug APK.
- `./gradlew test` — runs JVM unit tests in `app/src/test`.
- `./gradlew connectedAndroidTest` — runs instrumented tests on a connected device/emulator.
- `./gradlew lint` — runs Android lint checks.
- `./gradlew clean` — clears build outputs when troubleshooting.

Use `gradlew.bat` on Windows.

## Coding Style & Naming Conventions
- Language: Kotlin + Jetpack Compose.
- Follow Kotlin style defaults (4-space indentation, no tabs).
- Use `PascalCase` for classes/composables/files (e.g., `ProfileScreen.kt`), `camelCase` for methods/variables, and `UPPER_SNAKE_CASE` for constants.
- Keep composables small and screen-focused; extract reusable UI pieces when logic grows.
- Prefer immutable state and explicit data models in `data/model`.

Format using Android Studio’s Kotlin formatter before committing.

## Testing Guidelines
- Unit tests: JUnit (`app/src/test`), file names like `AuthViewModelTest.kt`.
- Instrumented tests: AndroidX/JUnit4 (`app/src/androidTest`), file names ending with `InstrumentedTest.kt`.
- Add or update tests for changed behavior, especially ViewModel logic and navigation-critical paths.

Minimum expectation: `./gradlew test` passes before opening a PR.

## Commit & Pull Request Guidelines
Current history is minimal (`first commit`), so apply a clear convention going forward:

- Commit messages: imperative, concise subject (e.g., `Add profile editing validation`).
- Keep commits focused; avoid mixing refactor and feature work in one commit.
- PRs should include: summary, scope, testing notes (`./gradlew test`, lint status), and screenshots/GIFs for UI changes.
- Link related issues/tasks when available.
