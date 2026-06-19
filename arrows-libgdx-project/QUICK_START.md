# Quick Start Guide

## Prerequisites
- **Java 11+**: `java -version` should show 11 or higher
- **Git**: For version control (optional)

## 5-Minute Setup

### 1. Extract the project
```bash
unzip arrows-libgdx-project.zip
cd arrows-libgdx-project
```

### 2. Build & Run Desktop Version
```bash
# Unix/macOS/Linux
./gradlew desktop:run

# Windows
gradlew.bat desktop:run
```

That's it! The game should start in a window.

## Using Makefile (Easier)

If you have `make` installed (macOS/Linux):

```bash
make run        # Build and run desktop version
make android    # Build Android APK
make install    # Build, install, and run on Android device
make help       # See all available commands
```

## Common Issues

### "Java not found"
Install JDK 11+ from https://adoptopenjdk.net

### "Permission denied: ./gradlew"
```bash
chmod +x gradlew
```

### "gradle-wrapper.jar not found"
```bash
./gradlew --refresh-dependencies
```

## Next Steps

1. **Try Desktop Version First**: 
   ```bash
   ./gradlew desktop:run
   ```

2. **Build Android APK**:
   ```bash
   ./gradlew android:assembleDebug
   ```
   Output: `android/build/outputs/apk/debug/android-debug.apk`

3. **Install on Phone** (requires Android SDK & USB debugging enabled):
   ```bash
   ./gradlew android:installDebug
   ./gradlew android:run
   ```

4. **Add Assets**:
   - Sounds: `android/assets/sounds/`
   - Textures: `android/assets/textures/`
   - Data: `android/assets/data/`

## Project Structure

```
core/          ← Game logic (Kotlin)
desktop/       ← Desktop launcher (LWJGL3)
android/       ← Android app
  └─ assets/   ← Game assets (sounds, textures, data)
```

## Game Controls

- **Tap/Click**: Select lines before they disappear
- **Score**: 10 points per line
- **Lives**: Start with 3, lose one per missed line

## File Locations After Build

- **Desktop JAR**: `desktop/build/libs/desktop-1.0.0.jar`
- **Android APK**: `android/build/outputs/apk/debug/android-debug.apk`

## Troubleshooting

### Can't find JAVA_HOME?
```bash
# Linux/macOS - Find Java path
which java

# Windows - Usually:
# C:\Program Files\Java\jdk-11\bin\java.exe
```

### Build hangs?
```bash
# Try with more verbosity
./gradlew --info desktop:run

# Or refresh dependencies
./gradlew --refresh-dependencies
```

### Want to see build errors?
```bash
# Verbose output
./gradlew desktop:build --stacktrace
```

## What's Next?

- **Read full README.md** for detailed documentation
- **Explore core/src/main/kotlin/** for game code
- **Add your own assets** to android/assets/
- **Customize game logic** in ArrowsGame.kt

## Getting Help

1. Check the terminal output for specific error messages
2. Look at the full README.md
3. Ensure Java 11+ is installed
4. Check gradle version: `./gradlew --version`

---

**Enjoy building! 🚀**
