# SuperRealisticPets

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template that includes Kotlin application launchers and an empty `ApplicationAdapter` implemented in Kotlin.

---

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

---

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.

Useful Gradle tasks and flags:

- `--continue`: errors will not stop the tasks from running.
- `--daemon`: Gradle daemon will be used to run chosen tasks.
- `--offline`: cached dependency archives will be used.
- `--refresh-dependencies`: forces validation of all dependencies. Useful for snapshots.
- `android:lint`: performs Android project validation.
- `build`: builds sources and archives of every project.
- `clean`: removes `build` folders.
- `eclipse`: generates Eclipse project data.
- `html:dist`: compiles GWT sources, output in `html/build/dist`.
- `html:superDev`: compiles GWT sources in SuperDev mode.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds runnable jar.
- `lwjgl3:run`: runs the desktop application.
- `test`: runs unit tests (if any).
- `./gradlew :core:clean :core:test :core:jacocoTestReport` : run test, and generate Jacoco test coverage


---

## Project Structure

```bash
SuperRealisticPets/
├── android/             # Android-specific code
│   ├── AndroidManifest.xml
│   ├── build.gradle
│   └── src/
│       └── io/github/super_auto_pets/android/
│           ├── AndroidLauncher.kt
│           ├── EditScreen.kt
│           ├── GameScreen.kt
│           ├── HighscoreScreen.kt
│           ├── Main.kt
│           ├── MainMenuScreen.kt
├── core/                # Core game logic
│   ├── build.gradle
│   └── src/
│       └── io/github/super_auto_pets/
│           ├── controller/
│           │   ├── BattleController.kt
│           │   ├── BuildPhase.kt
│           │   ├── GameMode.kt
│           │   ├── PlayerController.kt
│           │   └── ShopController.kt
│           ├── firebase/
│           │   ├── FirebaseHighscoreService.kt
│           │   └── HighscoreManager.kt
│           ├── interfaces/
│           │   └── HighscoreService.kt
│           ├── models/
│           │   ├── Battle.kt
│           │   ├── HighscoreEntry.kt
│           │   ├── Item.kt
│           │   ├── Player.kt
│           │   ├── Shop.kt
│           │   └── Sprite.kt
│           └── util/
│               └── AudioManager.kt
├── ios/                  # Not used
├── lwjgl3/               # Not used
│   ├── build.gradle
│   └── src/
│       └── io/github/super_auto_pets/lwjgl3/
│           └── Lwjgl3Launcher.kt
├── build.gradle          # Root Gradle build script
├── settings.gradle       # Gradle project settings
└── README.md             # You're reading it now
```

---

## How to Run

### Prerequisites
- JDK 11+
- Android SDK if building Android
- Gradle (or use the included wrapper)


### Android
Open the project in Android Studio and run the `android` module.


## Notes
- This game uses Firebase for storing and retrieving high scores.
- Multiplayer and singleplayer modes supported.
- Drag and Drop features for editing teams.

---

## Test

### Run tests
- `./gradlew :core:clean :core:test :core:jacocoTestReport` : run test, and generate Jacoco test coverage


### Test reports
- `core/build/reports/coverage/html/index.html` for code coverage report
-  `core/build/reports/tests/test/index.html` for test report

