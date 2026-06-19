package com.arrows.game.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import kotlin.math.sqrt

class Line(
    val startPoint: Vector2,
    val endPoint: Vector2,
    val duration: Float = 3f,
    val color: LineColor = LineColor.WHITE
) {
    // Current animation state
    private var currentLength = 0f
    private var elapsedTime = 0f
    private var isAnimatingIn = true
    
    var isDead = false
        private set
    var isClickable = true
        private set
    
    // Collision properties
    private val width = 5f
    
    private val clickListeners = mutableListOf<() -> Unit>()
    
    fun update(deltaTime: Float) {
        elapsedTime += deltaTime
        
        // Animation phase: first 0.3s animate line drawing
        val animationDuration = 0.3f
        
        if (isAnimatingIn && elapsedTime <= animationDuration) {
            val progress = elapsedTime / animationDuration
            currentLength = getLineLength() * progress
        } else if (isAnimatingIn && elapsedTime > animationDuration) {
            isAnimatingIn = false
            currentLength = getLineLength()
        }
        
        // Destruction phase: disappear after duration
        if (elapsedTime >= duration) {
            isDead = true
            isClickable = false
        }
    }
    
    fun render(shapeRenderer: ShapeRenderer) {
        if (isDead) return
        
        // Set color based on line state
        val (r, g, b) = when (color) {
            LineColor.WHITE -> Triple(1f, 1f, 1f)
            LineColor.RED -> Triple(1f, 0f, 0f)
            LineColor.GREEN -> Triple(0f, 1f, 0f)
            LineColor.BLUE -> Triple(0f, 0.5f, 1f)
            LineColor.YELLOW -> Triple(1f, 1f, 0f)
        }
        
        shapeRenderer.color.set(r, g, b, 0.8f)
        
        // Draw the animated line
        val direction = Vector2(endPoint).sub(startPoint).nor()
        val endPos = Vector2(startPoint).add(direction.x * currentLength, direction.y * currentLength)
        
        shapeRenderer.rectLine(startPoint, endPos, width)
    }
    
    fun containsPoint(point: Vector2): Boolean {
        if (!isClickable || isDead) return false
        
        // Point-to-line-segment distance
        val distance = distanceToPoint(point)
        return distance < width * 2 // Clickable area slightly larger than visual
    }
    
    private fun distanceToPoint(point: Vector2): Float {
        val dx = endPoint.x - startPoint.x
        val dy = endPoint.y - startPoint.y
        
        if (dx == 0f && dy == 0f) {
            return point.dst(startPoint)
        }
        
        val t = ((point.x - startPoint.x) * dx + (point.y - startPoint.y) * dy) / (dx * dx + dy * dy)
        val clampedT = t.coerceIn(0f, 1f)
        
        val closestX = startPoint.x + clampedT * dx
        val closestY = startPoint.y + clampedT * dy
        
        return sqrt((point.x - closestX) * (point.x - closestX) + (point.y - closestY) * (point.y - closestY))
    }
    
    private fun getLineLength(): Float {
        return startPoint.dst(endPoint)
    }
    
    fun onClicked() {
        clickListeners.forEach { it() }
    }
    
    fun addClickListener(listener: () -> Unit) {
        clickListeners.add(listener)
    }
    
    fun getProgress(): Float = (elapsedTime / duration).coerceIn(0f, 1f)
    
    enum class LineColor {
        WHITE, RED, GREEN, BLUE, YELLOW
    }
}
