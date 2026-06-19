package com.arrows.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.input.GestureDetector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.arrows.game.entities.Line
import com.arrows.game.managers.*
import com.arrows.game.systems.*

class ArrowsGame : ApplicationAdapter() {
    
    // Graphics
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var shapeRenderer: ShapeRenderer
    
    // Game managers
    private lateinit var stateManager: StateManager
    private lateinit var livesManager: LivesManager
    private lateinit var lineManager: LineManager
    private lateinit var inputManager: InputManager
    private lateinit var audioManager: AudioManager
    
    // Game state
    private val lines = mutableListOf<Line>()
    private var score = 0
    private var gameRunning = true
    
    override fun create() {
        // Initialize graphics
        val width = 1080f
        val height = 1920f
        
        camera = OrthographicCamera()
        viewport = FitViewport(width, height, camera)
        camera.setToOrtho(false, width, height)
        
        shapeRenderer = ShapeRenderer()
        
        // Initialize managers
        stateManager = StateManager()
        livesManager = LivesManager(startingLives = 3)
        lineManager = LineManager()
        inputManager = InputManager()
        audioManager = AudioManager()
        
        // Setup input
        Gdx.input.inputProcessor = GestureDetector(object : GestureDetector.GestureListener {
            override fun touchDown(x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val screenPos = Vector3(x, y, 0f)
                camera.unproject(screenPos)
                inputManager.onTouchDown(Vector2(screenPos.x, screenPos.y))
                handleTouchInput(Vector2(screenPos.x, screenPos.y))
                return true
            }

            override fun tap(x: Float, y: Float, count: Int, button: Int): Boolean = false
            override fun longPress(x: Float, y: Float): Boolean = false
            override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean = false
            override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean = false
            override fun panStop(x: Float, y: Float, pointer: Int, button: Int): Boolean = false
            override fun zoom(initialDistance: Float, distance: Float): Boolean = false
            override fun pinch(initialPointer1: Vector2?, initialPointer2: Vector2?, pointer1: Vector2?, pointer2: Vector2?): Boolean = false
            override fun pinchStop() {}
        })
    }
    
    private fun handleTouchInput(touchPos: Vector2) {
        // Check collision with lines
        for (line in lines) {
            if (line.isClickable && line.containsPoint(touchPos)) {
                line.onClicked()
                audioManager.playLineClickSound()
                score += 10
                break
            }
        }
    }
    
    override fun render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        
        val deltaTime = Gdx.graphics.deltaTime
        
        // Update
        update(deltaTime)
        
        // Draw
        draw()
    }
    
    private fun update(deltaTime: Float) {
        if (!gameRunning) return
        
        // Update lines
        lines.removeAll { it.isDead }
        lines.forEach { it.update(deltaTime) }
        
        // Update managers
        stateManager.update(deltaTime)
        livesManager.update(deltaTime)
        
        // Spawn new lines periodically
        lineManager.updateSpawnTimer(deltaTime)
        if (lineManager.shouldSpawn()) {
            val newLine = lineManager.spawnRandomLine(viewport.worldWidth, viewport.worldHeight)
            lines.add(newLine)
        }
        
        // Check game over
        if (livesManager.lives <= 0) {
            gameRunning = false
            stateManager.setState(GameState.GAME_OVER)
        }
    }
    
    private fun draw() {
        viewport.apply()
        camera.update()
        
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color.set(1f, 1f, 1f, 1f)
        
        // Draw all lines
        for (line in lines) {
            line.render(shapeRenderer)
        }
        
        shapeRenderer.end()
        
        // Draw UI (score, lives)
        drawUI()
    }
    
    private fun drawUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Draw lives as circles in top-left
        for (i in 0 until livesManager.lives) {
            val x = 50f + (i * 60f)
            val y = viewport.worldHeight - 50f
            shapeRenderer.color.set(1f, 0f, 0f, 1f)
            shapeRenderer.circle(x, y, 15f)
        }
        
        shapeRenderer.end()
    }
    
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }
    
    override fun dispose() {
        shapeRenderer.dispose()
        audioManager.dispose()
    }
}
