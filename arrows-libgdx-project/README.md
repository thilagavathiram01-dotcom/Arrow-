# Arrows Game - Kotlin + LibGDX

A complete port of the Arrows game from Unity C# to Kotlin using LibGDX framework. Buildable from command line.

## Project Structure

```
arrows-libgdx-project/
├── core/                  # Shared game logic (Kotlin)
│   ├── src/main/kotlin/com/arrows/game/
│   │   ├── ArrowsGame.kt             # Main game class
│   │   ├── managers/                 # Game managers
│   │   │   ├── StateManager.kt       # Game state management
│   │   │   ├── LivesManager.kt       # Lives tracking
│   │   │   ├── AudioManager.kt       # Sound effects & music
│   │   │   └── InputManager.kt       # Input handling
│   │   ├── entities/                 # Game entities
│   │   │   └── Line.kt               # Drawable line entity
│   │   └── systems/
│   │       └── LineManager.kt        # Line spawning system
│   └── build.gradle
│
├── desktop/               # Desktop (LWJGL3) launcher
│   ├── src/main/kotlin/com/arrows/game/
│   │   └── DesktopLauncher.kt        # Desktop entry point
│   └── build.gradle
│
├── android/               # Android launcher
│   ├── src/main/
│   │   ├── kotlin/com/arrows/game/
│   │   │   └── AndroidLauncher.kt    # Android entry point
│   │   ├── AndroidManifest.xml
│   │   └── res/
│   ├── assets/                        # Game assets (to be added)
│   │   ├── sounds/                   # Sound files (.wav, .mp3)
│   │   ├── textures/                 # Texture files (.png)
│   │   └── data/                     # Level data (.json)
│   └── build.gradle
│
├── build.gradle           # Root build config
├── settings.gradle        # Project structure
├── gradle.properties      # Gradle settings
├── gradlew               # Unix Gradle wrapper
├── gradlew.bat           # Windows Gradle wrapper
├── Makefile              # Build commands
└── README.md             # This file
```

## Requirements

- JDK 11 or higher
- Gradle 8.0+ (or use included wrapper)
- Android SDK (for Android builds)
- For Desktop: LWJGL3 (included via Gradle)

## Building

### Using Makefile (Recommended)

```bash
# Build desktop version
make desktop

# Build Android APK
make android

# Run desktop version
make run

# Clean build
make clean

# Build all
make all
```

### Using Gradle Directly

#### Desktop Build & Run
```bash
# On macOS/Linux
./gradlew desktop:run

# On Windows
gradlew.bat desktop:run

# Just build JAR
./gradlew desktop:build
```

#### Android Build
```bash
# Build APK
./gradlew android:assembleDebug

# Install on connected device
./gradlew android:installDebug
./gradlew android:run
```

#### Build APK for Release
```bash
./gradlew android:assembleRelease
# Output: android/build/outputs/apk/release/android-release.apk
```

## Game Features

### Core Gameplay
- Lines spawn from screen edges and animate towards center
- Player taps lines to destroy them before they disappear
- Gain 10 points per line destroyed
- Lose a life if a line completes without being tapped
- Game over when all 3 lives are lost

### Systems Implemented
- **StateManager**: Handles MENU → PLAYING → GAME_OVER states
- **LivesManager**: Tracks player lives with invulnerability frames
- **LineManager**: Spawns animated lines with random properties
- **AudioManager**: Plays sound effects (extensible)
- **InputManager**: Handles touch/click input
- **Collision Detection**: Point-to-line-segment distance calculation

## Adding Assets

### Audio Files
Place sound files in `android/assets/sounds/`:
- `click.wav` - Line tap sound
- `gameover.wav` - Game over sound
- `levelcomplete.wav` - Victory sound
- `background.mp3` - Background music

### Textures/Sprites
Place texture files in `android/assets/textures/`:
- Any PNG or JPG files referenced in code

### Level Data
Create JSON files in `android/assets/data/`:
```json
{
  "levels": [
    {
      "id": 1,
      "spawnRate": 0.8,
      "duration": 180,
      "name": "Level 1"
    }
  ]
}
```

## Code Architecture

### Entity-Component Pattern
- `Line` entity handles rendering and collision
- Managers handle specific systems (state, lives, audio)
- Main game loop coordinates all systems

### Collision Detection
Uses vector math for point-to-line-segment distance:
```kotlin
fun containsPoint(point: Vector2): Boolean {
    val distance = distanceToPoint(point)
    return distance < clickableArea
}
```

### Animation System
Lines animate in two phases:
1. **Drawing** (0-0.3s): Line grows from start to end point
2. **Existing** (0.3s-duration): Line visible and clickable
3. **Fading** (duration+): Line removed

## Development Workflow

### Adding New Features

#### 1. New Game State
```kotlin
// In StateManager.kt
enum class GameState {
    MENU, PLAYING, PAUSED, GAME_OVER, LEVEL_COMPLETE, NEW_STATE
}
```

#### 2. New Sound
```kotlin
// In AudioManager.kt
fun playNewSound() {
    val sound = loadSound("sounds/newsound.wav")
    sound?.play(soundVolume * masterVolume)
}
```

#### 3. New Line Type
```kotlin
// Extend Line class or create LineVariant
class SpecialLine(startPoint: Vector2, endPoint: Vector2) : Line(startPoint, endPoint)
```

### Testing Locally

```bash
# Build and run desktop version
./gradlew desktop:run

# Desktop build creates JAR at:
# desktop/build/libs/desktop-1.0.0.jar

# Run JAR directly
java -jar desktop/build/libs/desktop-1.0.0.jar
```

## Porting from Unity Assets

### Sprite Conversion
```bash
# Export from Unity as PNG/JPG
# Place in android/assets/textures/
# Reference in code:
# val texture = Texture(Gdx.files.internal("textures/sprite.png"))
```

### Shader Conversion
- Unity ShaderLab → OpenGL ES GLSL (if needed)
- Or replace with Canvas/Paint effects for simplicity

### Audio Conversion
- Unity WAV/MP3 → LibGDX compatible formats (WAV, MP3)
- No conversion needed for most files

## Common Issues & Solutions

### "Failed to load native library"
```bash
# Ensure LWJGL3 natives are downloaded
./gradlew --refresh-dependencies
```

### Android Build Fails
```bash
# Update Android SDK
# Ensure compileSdkVersion matches installed SDK

# Check NDK installation
./gradlew android:build --info
```

### AssetNotFound Exception
```kotlin
// Check file path is correct relative to assets/
// Valid: "sounds/click.wav"
// Invalid: "/sounds/click.wav" or "./sounds/click.wav"
```

## Performance Tips

- Line object pooling (reuse instead of create/destroy)
- Batch rendering using ShapeRenderer
- Limit simultaneous lines (adjust spawnInterval)
- Use appropriate deltaTime for frame-rate independence

## Future Enhancements

- [ ] Particle effects for line destruction
- [ ] Power-ups system
- [ ] Level progression
- [ ] Leaderboard system
- [ ] More animation styles
- [ ] Touch haptics feedback
- [ ] Screen rotation handling
- [ ] Save/load game state

## License

[Your License Here]

## Support

For issues or questions:
1. Check build output for specific errors
2. Verify all dependencies are installed
3. Ensure Kotlin/Gradle versions match
4. Check Android SDK tools are updated

## Building Docker Image (Optional)

```dockerfile
FROM openjdk:11-jre-slim
COPY . /app
WORKDIR /app
RUN apt-get update && apt-get install -y gradle
CMD ["gradle", "desktop:run"]
```

Build: `docker build -t arrows-game .`
Run: `docker run -it arrows-game`
