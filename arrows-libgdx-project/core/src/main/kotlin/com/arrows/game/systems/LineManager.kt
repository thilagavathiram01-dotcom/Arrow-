package com.arrows.game.systems

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.arrows.game.entities.Line

class LineManager {
    private var spawnTimer = 0f
    private var spawnInterval = 0.8f // Spawn a new line every 0.8 seconds
    private var lineCounter = 0
    
    fun updateSpawnTimer(deltaTime: Float) {
        spawnTimer += deltaTime
    }
    
    fun shouldSpawn(): Boolean {
        return spawnTimer >= spawnInterval
    }
    
    fun spawnRandomLine(screenWidth: Float, screenHeight: Float): Line {
        spawnTimer = 0f
        lineCounter++
        
        // Random spawn positions along edges
        val startPoint = when (MathUtils.random(0, 3)) {
            0 -> Vector2(0f, MathUtils.random(screenHeight)) // Left edge
            1 -> Vector2(screenWidth, MathUtils.random(screenHeight)) // Right edge
            2 -> Vector2(MathUtils.random(screenWidth), 0f) // Bottom edge
            else -> Vector2(MathUtils.random(screenWidth), screenHeight) // Top edge
        }
        
        // Random end point (moving towards center)
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        
        val endPoint = Vector2(
            centerX + MathUtils.random(-200f, 200f),
            centerY + MathUtils.random(-200f, 200f)
        )
        
        // Random color
        val colors = Line.LineColor.values()
        val randomColor = colors[MathUtils.random(colors.size - 1)]
        
        // Create line with random duration
        val duration = MathUtils.random(2f, 4f)
        
        return Line(
            startPoint = startPoint,
            endPoint = endPoint,
            duration = duration,
            color = randomColor
        )
    }
    
    fun spawnLineAtPosition(startX: Float, startY: Float, endX: Float, endY: Float): Line {
        return Line(
            startPoint = Vector2(startX, startY),
            endPoint = Vector2(endX, endY),
            duration = 3f,
            color = Line.LineColor.WHITE
        )
    }
    
    fun getLineCount(): Int = lineCounter
    
    fun resetSpawner() {
        spawnTimer = 0f
        lineCounter = 0
    }
    
    fun setSpawnInterval(interval: Float) {
        spawnInterval = interval.coerceAtLeast(0.2f)
    }
}
