# ToDo App – Kotlin Multiplatform (KMP)

Dieses Projekt dient als **Know-How Transfer für Kotlin Multiplatform (KMP)**.  
Es besteht aus einer einfachen ToDo-App, die auf **Android (Jetpack Compose)** und **iOS (SwiftUI)** läuft und den **Business-Code teilt**.

---

## Projektstruktur

```
ToDoApp/
├── shared/                         # Gemeinsamer Kotlin-Code (Business-Logik)
│   └── src/
│       ├── commonMain/             # Plattformübergreifend: Model, Repository, ViewModel
│       ├── androidMain/            # Android-spezifisch: actual Platform
│       └── iosMain/                # iOS-spezifisch: actual Platform, TodoStore (Callback-Bridge)
├── androidApp/                     # Android App (Jetpack Compose)
└── iosApp/                         # iOS App (SwiftUI + Xcode-Projekt)
```

---

## Voraussetzungen

| Tool | Mindestversion | Hinweis |
|------|---------------|---------|
| JDK | 17 | `java -version` |
| IntelliJ IDEA Ultimate | 2025.1+ | inkl. Kotlin & Android Plugin |
| Android SDK | API 36 (Android 16) | über IntelliJ SDK-Manager einrichten |
| Xcode | 26.4 | nur auf macOS für iOS-Build |

> **Kotlin Version:** 2.2.10 · **AGP:** 9.0.1 · **Gradle Wrapper:** 9.2.1 · **compileSdk/targetSdk:** 36 · **iOS Deployment Target:** 18.0

---

## Erste Einrichtung

### Android SDK in IntelliJ konfigurieren

Beim ersten Öffnen fragt IntelliJ nach dem Android SDK:

1. **Preferences → Appearance & Behavior → System Settings → Android SDK**
2. SDK-Pfad setzen (Standard: `~/Library/Android/sdk`)
3. Falls kein SDK vorhanden: über **SDK Tools** installieren

### Kotlin Multiplatform Plugin

In IntelliJ IDEA Ultimate ist das KMP-Plugin ab **Version 2023.3** direkt integriert.  
Prüfen unter: **Preferences → Plugins → Kotlin Multiplatform**

---

## Android App bauen & starten

### 1. In IntelliJ IDEA Ultimate öffnen

Öffne den Root-Ordner (`ToDoApp/`) in IntelliJ IDEA Ultimate.  
IntelliJ erkennt das Gradle-Projekt automatisch und importiert es.

### 2. Run-Konfiguration einrichten (IntelliJ)

1. **Run → Edit Configurations → + → Android App**
2. Module: `androidApp.app` wählen
3. Emulator oder verbundenes Gerät auswählen
4. **Run ▶** drücken

### 3. App bauen (Kommandozeile)

```bash
# Debug-APK bauen
./gradlew :androidApp:assembleDebug

# APK-Pfad: androidApp/build/outputs/apk/debug/androidApp-debug.apk

# Installieren & starten (Gerät/Emulator muss verbunden sein)
./gradlew :androidApp:installDebug
```

---

## iOS App bauen & starten

> Nur auf **macOS** mit installiertem **Xcode** möglich.

### 1. Shared Framework bauen

Xcode führt diesen Schritt automatisch über eine "Run Script" Build Phase aus.  
Manuell geht es so:

```bash
# Debug (Simulator – Apple Silicon)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Debug (Simulator – Intel)
./gradlew :shared:linkDebugFrameworkIosX64

# Release (Device – Apple Silicon)
./gradlew :shared:linkReleaseFrameworkIosArm64

# Alle Architekturen als XCFramework
./gradlew :shared:assembleSharedXCFramework
```

### 2. Xcode-Projekt öffnen

```bash
open iosApp/iosApp.xcodeproj
```

### 3. Team eintragen

In Xcode: **iosApp Target → Signing & Capabilities → Team** → dein Apple Developer Account eintragen.

### 4. App starten

- Simulator wählen und **Run ▶** drücken
- Xcode baut automatisch das KMP-Framework (Build Phase Script) und dann die Swift-App

---

## Architektur & KMP-Konzepte

### Was wird geteilt?

```
commonMain
├── TodoItem.kt          // Datenmodell
├── TodoRepository.kt    // Business-Logik mit StateFlow
├── TodoViewModel.kt     // State-Holder (Android nutzt ihn direkt)
└── Platform.kt          // expect-Deklaration
```

### expect / actual – Plattformunterschiede

Das `expect`/`actual`-Pattern ist das Kernkonzept für plattformspezifischen Code:

```kotlin
// commonMain/Platform.kt
expect class Platform() {
    val name: String
}

// androidMain/Platform.android.kt
actual class Platform actual constructor() {
    actual val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

// iosMain/Platform.ios.kt
actual class Platform actual constructor() {
    actual val name: String = UIDevice.currentDevice.systemName() + " " + ...
}
```

### StateFlow & reaktive Architektur

```
Repository (StateFlow) → Android: collectAsState() in Compose
                       → iOS:     TodoStore (Callback-Bridge in iosMain)
```

### iOS-Bridge: Warum TodoStore?

Swift kann Kotlin Coroutines/StateFlow nicht direkt konsumieren.  
Der `TodoStore` (in `iosMain`) wrapped den `StateFlow` in einen **Callback**:

```kotlin
// iosMain/TodoStore.kt
class TodoStore(private val onUpdate: (List<TodoItem>) -> Unit) {
    init {
        repository.todos.onEach { todos -> onUpdate(todos) }.launchIn(scope)
    }
}
```

Swift nutzt diesen als `ObservableObject`:
```swift
store = TodoStore { [weak self] items in
    self?.todos = items as? [TodoItem] ?? []
}
```

---

## Nützliche Gradle Tasks

```bash
# Alle verfügbaren Tasks anzeigen
./gradlew tasks

# Shared-Modul kompilieren (alle Targets)
./gradlew :shared:build

# Android Tests
./gradlew :androidApp:test

# XCFramework für alle iOS-Architekturen
./gradlew :shared:assembleSharedXCFramework
# Output: shared/build/XCFrameworks/debug/Shared.xcframework
```

---

## Weiterführende Ressourcen

- [Kotlin Multiplatform Doku](https://kotlinlang.org/docs/multiplatform.html)
- [KMP Wizard (Projektgenerator)](https://kmp.jetbrains.com)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [KMP-NativeCoroutines (Alternativer Swift-Flow-Bridge)](https://github.com/rickclephas/KMP-NativeCoroutines)
