package com.arrows.game.managers

import com.badlogic.gdx.math.Vector2

class InputManager {
    private val touchListeners = mutableListOf<(Vector2) -> Unit>()
    private val dragListeners = mutableListOf<(Vector2, Vector2) -> Unit>()
    
    private var lastTouchPos = Vector2()
    private var isTouching = false
    
    fun onTouchDown(position: Vector2) {
        lastTouchPos.set(position)
        isTouching = true
        touchListeners.forEach { it(position) }
    }
    
    fun onTouchDrag(position: Vector2) {
        if (isTouching) {
            dragListeners.forEach { it(lastTouchPos, position) }
            lastTouchPos.set(position)
        }
    }
    
    fun onTouchUp(position: Vector2) {
        isTouching = false
    }
    
    fun onTouchListener(listener: (Vector2) -> Unit) {
        touchListeners.add(listener)
    }
    
    fun onDragListener(listener: (Vector2, Vector2) -> Unit) {
        dragListeners.add(listener)
    }
    
    fun isTouchingScreen(): Boolean = isTouching
    
    fun getLastTouchPosition(): Vector2 = Vector2(lastTouchPos)
}
