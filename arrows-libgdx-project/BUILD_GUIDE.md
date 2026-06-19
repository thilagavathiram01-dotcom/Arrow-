# Complete Build Guide

This guide covers building Arrows for all platforms (Desktop, Android Debug, Android Release).

## System Requirements

### All Platforms
- **Java**: Version 11 or higher
- **Git**: For cloning (optional)

### Desktop (Linux/macOS/Windows)
- No additional requirements (LWJGL3 handled by Gradle)

### Android
- **Android SDK**: API 21+ (installed via Android Studio or standalone)
- **Android NDK**: Version 25.1+ (for native code)
- **Gradle**: 8.0+ (included via wrapper)

## Verification

Before building, verify your setup:

```bash
# Check Java version (must be 11+)
java -version

# Check if JAVA_HOME is set
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME%  # Windows

# Check Gradle
./gradlew --version
```

## Desktop Build (All Platforms)

### Quick Start
```bash
./gradlew desktop:run
```

This will:
1. Compile Kotlin code
2. Download dependencies
3. Package JAR file
4. Launch the game window

### Build Only (No Run)
```bash
./gradlew desktop:build
```

Output: `desktop/build/libs/desktop-1.0.0.jar`

### Run Existing JAR
```bash
java -jar desktop/build/libs/desktop-1.0.0.jar
```

### Build with Specific JVM Memory
```bash
# Increase heap size if running out of memory
./gradlew -Dorg.gradle.jvmargs="-Xmx2048m" desktop:run
```

### Troubleshooting Desktop Build

#### Native Library Loading Error
```
Exception in thread "main" java.lang.UnsatisfiedLinkError: 
com.sun.jna.Native.open
```

**Solution**:
```bash
./gradlew --refresh-dependencies desktop:clean desktop:build
```

#### OutOfMemoryError
```
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
```

**Solution**:
```bash
./gradlew -Dorg.gradle.jvmargs="-Xmx4096m" desktop:run
```

#### Slow First Build
First build is slow (10-20 minutes) because it downloads all dependencies.
Subsequent builds are faster (30 seconds - 2 minutes).

## Android Debug Build

### Prerequisites
1. **Android SDK**: Set ANDROID_HOME environment variable
   ```bash
   # macOS/Linux (add to ~/.bashrc or ~/.zshrc)
   export ANDROID_HOME=~/Library/Android/sdk
   
   # Windows (in Environment Variables)
   ANDROID_HOME=C:\Users\YourUsername\AppData\Local\Android\sdk
   ```

2. **Verify Setup**:
   ```bash
   echo $ANDROID_HOME
   ls $ANDROID_HOME/platforms  # Should list API levels
   ```

### Build Debug APK
```bash
./gradlew android:assembleDebug
```

Output: `android/build/outputs/apk/debug/android-debug.apk`

### Build and Install on Device
```bash
# Prerequisites: 
# 1. USB cable connected
# 2. USB debugging enabled on device
# 3. Device driver installed (Windows)

./gradlew android:installDebug
```

### Build, Install, and Run
```bash
./gradlew android:installDebug android:run
```

### View Device Logs
```bash
# In another terminal while app is running
adb logcat com.arrows.game:*

# Full logcat (more verbose)
adb logcat
```

### List Connected Devices
```bash
adb devices

# Output example:
# List of attached devices
# emulator-5554          device
# ZY2234H7QQ             device
```

### Debug-Specific Options

```bash
# Build with debug symbols
./gradlew android:assembleDebug --info

# Skip tests
./gradlew android:assembleDebug --exclude-task test

# Run on specific device
./gradlew android:installDebug -Pandroid.selectDevice=<device_id>
```

## Android Release Build

### Prerequisites
- **Keystore**: For signing APK (see Signing section below)
- **Version**: Update in android/build.gradle

### Build Release APK (Unsigned)
```bash
./gradlew android:assembleRelease
```

Output: `android/build/outputs/apk/release/android-release-unsigned.apk`

### Create Keystore (First Time Only)
```bash
# Generate new keystore
keytool -genkey -v -keystore ~/arrows.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias arrows

# You'll be prompted for:
# - Password (remember this!)
# - Your name
# - Organization
# - Location
# - Country code
```

### Build Signed Release APK
Create file `local.properties` in project root:

```properties
# local.properties
storeFile=/path/to/arrows.keystore
storePassword=your_password
keyAlias=arrows
keyPassword=your_password
```

Then build:
```bash
./gradlew android:assembleRelease -Psigned=true
```

Or update `android/build.gradle` with signing config:

```gradle
signingConfigs {
    release {
        storeFile file(System.getenv('HOME') + '/arrows.keystore')
        storePassword System.getenv('KEYSTORE_PASSWORD')
        keyAlias System.getenv('KEY_ALIAS')
        keyPassword System.getenv('KEY_PASSWORD')
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
    }
}
```

### Optimize Release Build
```bash
# Enable ProGuard/R8 obfuscation
./gradlew android:assembleRelease

# Result: Smaller, faster APK
```

## Using Make Commands

If `make` is installed:

```bash
make help          # Show all commands
make desktop       # Build desktop
make android       # Build debug APK
make run           # Build & run desktop
make install       # Build, install & run on device
make release-apk   # Build release APK
make clean         # Clean build artifacts
```

## CI/CD Build Commands

### GitHub Actions Example
```yaml
name: Build Arrows Game

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '11'
      
      - name: Build desktop
        run: ./gradlew desktop:build
      
      - name: Build Android
        run: ./gradlew android:assembleDebug
      
      - name: Upload APK
        uses: actions/upload-artifact@v2
        with:
          name: android-apk
          path: android/build/outputs/apk/debug/android-debug.apk
```

### GitLab CI Example
```yaml
image: openjdk:11

stages:
  - build

build_desktop:
  stage: build
  script:
    - ./gradlew desktop:build
  artifacts:
    paths:
      - desktop/build/libs/

build_android:
  stage: build
  script:
    - ./gradlew android:assembleDebug
  artifacts:
    paths:
      - android/build/outputs/
```

## Advanced Build Options

### Custom Build Properties

Create `gradle.properties`:

```properties
# Memory settings
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m

# Parallel builds
org.gradle.parallel=true
org.gradle.workers.max=4

# Caching
org.gradle.caching=true

# Offline mode (use cached dependencies)
org.gradle.offline=false
```

### Incremental Builds
```bash
# Only rebuild changed files
./gradlew build --build-cache

# Show build dependencies
./gradlew dependencies
```

### Custom Gradle Tasks

Add to `build.gradle`:

```gradle
task runGame {
    dependsOn 'desktop:run'
    doLast {
        println 'Game running...'
    }
}

task buildAll {
    dependsOn 'desktop:build', 'android:assembleDebug'
}
```

Run custom tasks:
```bash
./gradlew runGame
./gradlew buildAll
```

## Build Troubleshooting

### Issue: "Gradle Daemon stopped"
```bash
./gradlew --stop
./gradlew desktop:run
```

### Issue: Out of Memory During Build
```bash
# Increase heap size
export GRADLE_OPTS="-Xmx4096m"
./gradlew desktop:build
```

### Issue: Android SDK Not Found
```bash
# Set ANDROID_HOME
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=~/Android/Sdk              # Linux
set ANDROID_HOME=C:\Android\sdk                # Windows
```

### Issue: Old Dependencies Causing Issues
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches
./gradlew clean
./gradlew build --refresh-dependencies
```

### Issue: Slow Network During Build
```bash
# Use offline mode (after first successful build)
./gradlew build --offline

# Or use dependency caching
./gradlew build --build-cache
```

## Build Optimization Tips

1. **Use Gradle Daemon** (default): Keeps JVM running between builds
   ```bash
   # Disable if causing issues
   ./gradlew --no-daemon build
   ```

2. **Parallel Compilation**: Speeds up multi-module builds
   ```bash
   ./gradlew build --parallel
   ```

3. **Configure Heap Properly**:
   ```properties
   # gradle.properties
   org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
   ```

4. **Update Gradle**:
   ```bash
   ./gradlew wrapper --gradle-version=8.1
   ```

## Build Verification

### Test Build Locally
```bash
# Desktop
./gradlew desktop:run
# Press ESC to close

# Android
./gradlew android:assembleDebug
ls android/build/outputs/apk/debug/android-debug.apk
```

### Verify APK Contents
```bash
# List files in APK
unzip -l android/build/outputs/apk/debug/android-debug.apk

# Check file size
ls -lh android/build/outputs/apk/debug/android-debug.apk
```

## Distribution

### Desktop JAR
```bash
# Create standalone JAR
./gradlew desktop:build

# Distribute
cp desktop/build/libs/desktop-1.0.0.jar ~/arrows.jar

# Users run with:
java -jar arrows.jar
```

### Android APK
```bash
# Debug APK (testing)
cp android/build/outputs/apk/debug/android-debug.apk ~/arrows-debug.apk

# Release APK (distribution)
# Build according to "Android Release Build" section
cp android/build/outputs/apk/release/android-release.apk ~/arrows.apk
```

### Publish to Play Store
1. Build release APK (follow section above)
2. Go to Google Play Console
3. Create new app
4. Upload APK to internal testing
5. Collect feedback
6. Publish to production

## Next Steps

1. **First Time?** Start with: `./gradlew desktop:run`
2. **Want Mobile?** Follow: "Android Debug Build"
3. **Ready to Release?** Follow: "Android Release Build"
4. **Having Issues?** Check: "Build Troubleshooting"

---

**Happy building! 🎉**
