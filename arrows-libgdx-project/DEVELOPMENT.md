# Development Guide

This guide helps you understand the codebase and add new features.

## Code Organization

### Core Game Loop (ArrowsGame.kt)

```kotlin
override fun create() {
    // Initialize everything
    // Called once on startup
}

override fun render() {
    // Called every frame
    update(deltaTime)  // Update game state
    draw()             // Render to screen
}
```

### Game Managers

#### StateManager
Tracks game state: MENU → PLAYING → PAUSED → GAME_OVER

```kotlin
val stateManager = StateManager()
stateManager.setState(GameState.PLAYING)
stateManager.getState() // Returns current state
```

#### LivesManager
Manages player lives and invulnerability

```kotlin
livesManager.loseLife()     // Lose one life
livesManager.getLives()     // Get current lives
livesManager.isInvulnerable() // Check if immune
```

#### LineManager
Spawns animated lines

```kotlin
val line = lineManager.spawnRandomLine(screenWidth, screenHeight)
lineManager.setSpawnInterval(0.5f) // Spawn every 0.5 seconds
```

#### AudioManager
Plays sound effects

```kotlin
audioManager.playLineClickSound()
audioManager.setMasterVolume(0.8f)
```

#### InputManager
Handles user input

```kotlin
inputManager.onTouchListener { position ->
    // Handle touch at position
}
```

## Adding Features

### 1. New Game State

Add to `StateManager.kt`:

```kotlin
enum class GameState {
    MENU, PLAYING, PAUSED, GAME_OVER, LEVEL_COMPLETE, SETTINGS
}
```

Then handle it in `ArrowsGame.kt`:

```kotlin
private fun handleStateChange(newState: GameState) {
    when (newState) {
        GameState.SETTINGS -> showSettingsMenu()
        else -> {}
    }
}
```

### 2. New Sound Effect

Add to `AudioManager.kt`:

```kotlin
fun playPowerUpSound() {
    val sound = loadSound("sounds/powerup.wav")
    sound?.play(soundVolume * masterVolume)
}
```

Call from game:

```kotlin
audioManager.playPowerUpSound()
```

### 3. New Line Type

Create in `entities/`:

```kotlin
class SpecialLine(
    startPoint: Vector2,
    endPoint: Vector2,
    val specialPower: PowerUp
) : Line(startPoint, endPoint) {
    
    override fun render(shapeRenderer: ShapeRenderer) {
        // Custom rendering
        super.render(shapeRenderer)
        // Draw special indicator
    }
}
```

### 4. New UI Element

Add rendering in `ArrowsGame.kt` `drawUI()`:

```kotlin
private fun drawUI() {
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
    
    // Draw existing UI
    // ...
    
    // Draw your new element
    shapeRenderer.color.set(0f, 1f, 0f, 1f)
    shapeRenderer.rect(x, y, width, height)
    
    shapeRenderer.end()
}
```

### 5. New Manager System

Create `managers/NewManager.kt`:

```kotlin
class NewManager {
    private var state = 0f
    
    fun update(deltaTime: Float) {
        state += deltaTime
    }
    
    fun getValue(): Float = state
}
```

Initialize in `ArrowsGame.create()`:

```kotlin
private lateinit var newManager: NewManager

override fun create() {
    // ...
    newManager = NewManager()
}
```

Update in `ArrowsGame.update()`:

```kotlin
private fun update(deltaTime: Float) {
    // ...
    newManager.update(deltaTime)
}
```

## Modifying Game Mechanics

### Change Spawn Rate

In `LineManager.kt`:

```kotlin
init {
    spawnInterval = 0.5f  // Spawn every 0.5 seconds (default: 0.8s)
}
```

Or dynamically:

```kotlin
lineManager.setSpawnInterval(0.3f)  // Faster as game progresses
```

### Change Line Duration

In `ArrowsGame.kt` `update()`:

```kotlin
val newLine = Line(
    startPoint = startPoint,
    endPoint = endPoint,
    duration = 5f,  // Change from default 3f
    color = color
)
```

### Change Points Per Line

In `ArrowsGame.kt` `handleTouchInput()`:

```kotlin
score += 25  // Changed from 10
```

### Change Starting Lives

In `ArrowsGame.kt` `create()`:

```kotlin
livesManager = LivesManager(startingLives = 5)  // Changed from 3
```

## Rendering Custom Shapes

LibGDX ShapeRenderer supports:

```kotlin
shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

// Circles
shapeRenderer.circle(x, y, radius)

// Rectangles
shapeRenderer.rect(x, y, width, height)

// Polygons
shapeRenderer.polygon(vertices)

// Lines
shapeRenderer.line(x1, y1, x2, y2)

// Curves (approximate)
// Use multiple line segments

shapeRenderer.end()
```

## Using Textures

Load textures:

```kotlin
val texture = Texture(Gdx.files.internal("textures/sprite.png"))
```

Draw with SpriteBatch:

```kotlin
val batch = SpriteBatch()

batch.begin()
batch.draw(texture, x, y, width, height)
batch.end()

batch.dispose()
```

## Input Handling

Detect touches:

```kotlin
inputManager.onTouchListener { position ->
    println("Touched at ${position.x}, ${position.y}")
}
```

Detect drags:

```kotlin
inputManager.onDragListener { from, to ->
    println("Dragged from ${from.x}, ${from.y} to ${to.x}, ${to.y}")
}
```

Keyboard input (add to input processor):

```kotlin
override fun keyDown(keycode: Int): Boolean {
    when (keycode) {
        Input.Keys.BACK -> handleBackPress()
        Input.Keys.SPACE -> handleSpacePress()
    }
    return true
}
```

## Performance Optimization

### Object Pooling

Instead of creating/destroying lines:

```kotlin
class LinePool {
    private val pool = mutableListOf<Line>()
    
    fun obtain(): Line = if (pool.isEmpty()) {
        Line(Vector2.Zero, Vector2.Zero)
    } else {
        pool.removeAt(pool.size - 1)
    }
    
    fun free(line: Line) {
        pool.add(line)
    }
}
```

### Batch Rendering

Instead of individual shape renders:

```kotlin
// Wrong: slow
for (line in lines) {
    shapeRenderer.begin()
    line.render(shapeRenderer)
    shapeRenderer.end()
}

// Right: fast
shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
for (line in lines) {
    line.render(shapeRenderer)  // No begin/end inside
}
shapeRenderer.end()
```

### Limit Simultaneous Lines

In `LineManager.kt`:

```kotlin
fun shouldSpawn(): Boolean {
    return spawnTimer >= spawnInterval && activeLineCount < MAX_LINES
}

companion object {
    const val MAX_LINES = 10
}
```

## Testing Locally

### Desktop Testing
```bash
./gradlew desktop:run
```

### Android Device Testing
```bash
# Connect device via USB
# Enable USB debugging on device
./gradlew android:installDebug
./gradlew android:run

# View logs
adb logcat com.arrows.game:*
```

## Common Patterns

### Observer Pattern (Event System)

```kotlin
class EventManager {
    private val listeners = mutableListOf<(Event) -> Unit>()
    
    fun subscribe(listener: (Event) -> Unit) {
        listeners.add(listener)
    }
    
    fun emit(event: Event) {
        listeners.forEach { it(event) }
    }
}
```

### Singleton Pattern (Manager)

```kotlin
object GameSettings {
    var difficulty = 1f
    var soundEnabled = true
}
```

### Component Pattern (Entity Systems)

```kotlin
interface GameComponent {
    fun update(deltaTime: Float)
    fun render(renderer: ShapeRenderer)
}

class GameEntity : GameComponent {
    private val components = mutableListOf<GameComponent>()
    
    override fun update(deltaTime: Float) {
        components.forEach { it.update(deltaTime) }
    }
    
    override fun render(renderer: ShapeRenderer) {
        components.forEach { it.render(renderer) }
    }
}
```

## Debugging

### Print Logs

```kotlin
println("Debug: score = $score")
```

### Android Logcat

```bash
adb logcat | grep com.arrows.game
```

### Desktop Debug Mode

Add to build.gradle:

```gradle
tasks.withType(JavaCompile) {
    options.debug = true
}
```

## Contributing Code

1. **Follow Kotlin style guide**:
   - 4-space indentation
   - camelCase for variables/functions
   - PascalCase for classes
   - UPPER_CASE for constants

2. **Comment complex logic**:
   ```kotlin
   // Clamp velocity to prevent physics instability
   velocity.limit(MAX_VELOCITY)
   ```

3. **Use meaningful names**:
   ```kotlin
   // Good
   val lineClickArea = 20f
   
   // Bad
   val area = 20f
   ```

4. **Keep functions small**:
   - One responsibility per function
   - Max 20-30 lines typically

## Useful LibGDX Resources

- https://libgdx.com/
- https://libgdx.com/wiki/
- https://github.com/libgdx/libgdx/wiki

## Next Steps

1. Run the game: `./gradlew desktop:run`
2. Modify values (spawn rate, lives, score)
3. Add a new sound effect
4. Create a new line type
5. Add a UI element
6. Build for Android: `./gradlew android:assembleDebug`

Happy coding! 🚀
