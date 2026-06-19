package com.arrows.game.managers

class LivesManager(startingLives: Int = 3) {
    var lives: Int = startingLives
        private set
    
    private val livesChangeListeners = mutableListOf<(Int) -> Unit>()
    private var invulnerableTime = 0f
    private val invulnerabilityDuration = 0.5f
    
    fun loseLife() {
        if (!isInvulnerable()) {
            lives--
            invulnerableTime = invulnerabilityDuration
            notifyListeners()
        }
    }
    
    fun gainLife() {
        lives++
        notifyListeners()
    }
    
    fun resetLives(newLiveCount: Int = 3) {
        lives = newLiveCount
        invulnerableTime = 0f
        notifyListeners()
    }
    
    fun isAlive(): Boolean = lives > 0
    
    fun isInvulnerable(): Boolean = invulnerableTime > 0f
    
    fun onLivesChange(listener: (Int) -> Unit) {
        livesChangeListeners.add(listener)
    }
    
    private fun notifyListeners() {
        livesChangeListeners.forEach { it(lives) }
    }
    
    fun update(deltaTime: Float) {
        if (invulnerableTime > 0f) {
            invulnerableTime -= deltaTime
        }
    }
}
