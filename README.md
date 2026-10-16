# SheFarm AI

An offline Android farm workspace for women farmers, built with Kotlin, Jetpack
Compose, ViewModels, coroutines and Room. Android 7.0 (API 24) or newer is required.
The existing Kenyan locations and KSh currency are retained.

## Features

- **Dashboard:** current financial totals, active produce count and recent activity.
- **Marketplace:** create, edit, search, filter and delete local produce listings;
  track Available, Reserved and Sold status.
- **Finances:** record revenue and expenses, edit/delete transactions, filter by
  type and calculate net profit and profit margin. Editing preserves the original date.
- **Assistant:** offline topic-based responses for farming, accounting and selling;
  locally saved conversations and persistent clear-history action. Failed writes
  preserve the input and offer a retry.
- **Guides:** six built-in guides, crop/text search, category filters and detail dialogs.

New installations start empty. **Load sample farm (demo records)** on the empty
dashboard explicitly adds example data. Every sample description starts with
`[DEMO]`. Visiting a screen or restarting the app never creates records, so deleted
data stays deleted. The sample button is available again if both collections are empty.

## Run in Android Studio

1. Open this repository folder in Android Studio.
2. Use the bundled JDK 25 as the Gradle JDK, as specified by the repository's daemon toolchain.
3. Install Android SDK Platform 35 and Build Tools 36.0.0 using SDK Manager.
4. Let Gradle sync, choose an emulator or USB-connected device, then run **app**.

The first build downloads Gradle and Maven dependencies. No API key, server or
internet permission is needed by the app itself.

## Build and verify on Windows

From the repository directory in PowerShell:

```powershell
.\scripts\verify.ps1
```

The script uses `JAVA_HOME` and `ANDROID_HOME` if set, otherwise discovers the
standard Android Studio and SDK installations. Alternatively:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest --no-daemon
.\gradlew.bat :app:lintDebug --no-daemon
```

Install the debug APK:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
```

With a running emulator or authorized USB device, run actual Android tests:

```powershell
.\scripts\verify.ps1 -Connected
```

Outputs:

- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Unit test report: `app/build/reports/tests/testDebugUnitTest/index.html`
- Lint report: `app/build/reports/lint-results-debug.html`
- Device test report: `app/build/reports/androidTests/connected/debug/index.html`

The build uses one worker and an in-process Kotlin compiler to limit memory use.
Only nine selected extended Material icons are included as source, preserving
their Apache 2.0 notices; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Architecture

`ui/screens` and `ui/components` render Compose UI. ViewModels expose `StateFlow`
and delegate operations to repository interfaces in `data/repository`. Room DAOs
and entities in `data/local` persist listings and finances. Chat history uses a
private local preferences store with complete-exchange writes. The guidance and
assistant topic libraries are bundled with the application.

Unit tests exercise calculations, CRUD, date/status preservation, filtering,
invalid numeric inputs, storage failures, chat persistence and retry behavior.
Android tests exercise real Room/preferences storage and Compose interactions.
Compiling Android tests alone does not mean they have run on a device.

## Scope and limits

This is a working offline hackathon application. It has no accounts, shared
marketplace, buyer matching, payments, cloud synchronization, live prices or
weather, grant verification, or generative AI model. The assistant uses keyword
topic selection and cannot diagnose crop problems or interpret arbitrary questions.
The bundled guides are general educational content; local crop/soil conditions and
product labels determine practical treatment choices.

Financial totals cover all saved transactions; there is no season/date filter.
Data stays in app-private local storage (subject to Android system backup settings).
Uninstalling or clearing app storage removes it. Chat clearing removes saved chat,
but does not delete financial records or listings. Release builds require your own
signing configuration before distribution.

See [DEBUGGING.md](DEBUGGING.md) for changes and verification evidence.
