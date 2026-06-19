# Game Assets Directory

Add your game assets here. The application will load them at runtime.

## Directory Structure

```
android/assets/
├── sounds/          - Audio files
├── textures/        - Image files
└── data/            - Game data (JSON, etc)
```

## Supported Formats

### Sounds
- WAV (.wav)
- MP3 (.mp3)
- OGG (.ogg)

### Textures
- PNG (.png)
- JPG (.jpg)

### Data
- JSON (.json)
- Properties (.properties)

## Recommended Sizes

### Audio
- Sample rate: 44.1kHz or 48kHz
- Bitrate: 128-256 kbps (MP3) or lossless (WAV)
- Duration: Keep under 5MB per file for performance

### Textures
- Format: PNG with transparency for best quality
- Size: Power of 2 (512x512, 1024x1024, etc) for optimal rendering
- Resolution: Match your target device DPI (1x for baseline)

## Asset Naming Convention

```
sounds/
├── click.wav
├── gameover.wav
├── levelcomplete.wav
└── background.mp3

textures/
├── background.png
├── ui_button.png
└── particle.png

data/
├── levels.json
├── config.json
└── highscores.json
```

## Loading Assets in Code

### Audio
```kotlin
// In AudioManager.kt
fun playCustomSound(soundFile: String) {
    val sound = loadSound("sounds/$soundFile")
    sound?.play(soundVolume * masterVolume)
}

// Usage
audioManager.playCustomSound("click.wav")
```

### Textures
```kotlin
// In any game component
val texture = Texture(Gdx.files.internal("textures/background.png"))

// Use in rendering
batch.draw(texture, x, y, width, height)
```

### Data (JSON)
```kotlin
// Add JSON parsing dependency in build.gradle
// implementation 'com.google.code.gson:gson:2.10.1'

import com.google.gson.Gson

val json = Gdx.files.internal("data/levels.json").readString()
val levels = Gson().fromJson(json, LevelData::class.java)
```

## Performance Tips

1. **Compress audio**: Use MP3 for background music, WAV for short effects
2. **Optimize images**: Use PNGs with optimized compression
3. **Stream large files**: Don't load everything at once
4. **Use asset manager**: For complex asset dependencies

```kotlin
// Example: AssetManager usage
val assetManager = AssetManager()
assetManager.load("sounds/click.wav", Sound::class.java)
assetManager.finishLoading()
val sound = assetManager.get("sounds/click.wav", Sound::class.java)
```

## Common Issues

### Asset Not Found Exception
```
com.badlogic.gdx.utils.GdxRuntimeException: 
Couldn't load file: sounds/click.wav
```

**Solution**: Check the exact path in the assets directory
- ✓ Correct: `sounds/click.wav`
- ✗ Incorrect: `/sounds/click.wav` or `sounds/click.WAV`

### Audio Plays But Sounds Wrong
- Check sample rate matches (44.1kHz or 48kHz)
- Try mono instead of stereo for short effects
- Use normalized audio (0dB peak)

### Texture Looks Blurry
- Ensure image size is power of 2 (256, 512, 1024)
- Increase DPI or use higher resolution
- Use PNG instead of JPG for crisp edges

## Migration from Unity Assets

### Export from Unity
1. Audio: Export as WAV (File → Export)
2. Sprites: Export as PNG (Sprite → Export)
3. Data: Export ScriptableObject as JSON

### Conversion Tools
- **Audio**: ffmpeg (convert formats)
- **Images**: ImageMagick or Photoshop (resize to power of 2)
- **Data**: Online JSON converters

```bash
# Convert MP3 to WAV with ffmpeg
ffmpeg -i input.mp3 -acodec pcm_s16le -ar 44100 output.wav

# Resize image to power of 2
convert input.png -resize 512x512 output.png
```

## Asset Checklist

Before building for release:

- [ ] All referenced sounds exist
- [ ] All referenced images exist
- [ ] Audio files are < 5MB each
- [ ] Images are power-of-2 sized
- [ ] No unused assets (saves APK size)
- [ ] Asset names match code references
- [ ] Audio is properly normalized
- [ ] Copyright/licenses included for external assets

## Adding New Asset Type

### Custom Texture Loader
```kotlin
class TextureLoader {
    fun loadTexture(path: String): Texture? {
        return try {
            Texture(Gdx.files.internal(path))
        } catch (e: Exception) {
            null
        }
    }
}
```

### Custom Audio System
```kotlin
class SoundPlayer {
    private val sounds = mutableMapOf<String, Sound>()
    
    fun loadAndPlay(path: String) {
        if (!sounds.containsKey(path)) {
            sounds[path] = Gdx.audio.newSound(Gdx.files.internal(path))
        }
        sounds[path]?.play()
    }
}
```

## More Help

- LibGDX File Handling: https://libgdx.com/wiki/file-handling
- Asset Management: https://libgdx.com/wiki/managing-assets
- Audio Guide: https://libgdx.com/wiki/sound-effects

---

**Ready to add your assets? Start by placing files in the appropriate directories!**
