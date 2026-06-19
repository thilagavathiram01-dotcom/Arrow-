package com.arrows.game.managers

enum class GameState {
    MENU, PLAYING, PAUSED, GAME_OVER, LEVEL_COMPLETE
}

class StateManager {
    private var currentState: GameState = GameState.MENU
    private val stateListeners = mutableListOf<(GameState) -> Unit>()
    private var stateTimer = 0f
    
    fun setState(newState: GameState) {
        if (currentState != newState) {
            currentState = newState
            notifyListeners()
        }
    }
    
    fun getState(): GameState = currentState
    
    fun isPlaying(): Boolean = currentState == GameState.PLAYING
    
    fun isPaused(): Boolean = currentState == GameState.PAUSED
    
    fun onStateChange(listener: (GameState) -> Unit) {
        stateListeners.add(listener)
    }
    
    private fun notifyListeners() {
        stateListeners.forEach { it(currentState) }
    }
    
    fun update(deltaTime: Float) {
        stateTimer += deltaTime
    }
    
    fun getStateTime(): Float = stateTimer
    
    fun resetStateTime() {
        stateTimer = 0f
    }
}
