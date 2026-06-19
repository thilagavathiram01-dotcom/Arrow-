package com.arrows.game.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Disposable

class AudioManager : Disposable {
    private var masterVolume = 1f
    private var soundVolume = 0.8f
    private var musicVolume = 0.6f
    
    // Sound effect cache
    private val soundCache = mutableMapOf<String, Sound>()
    private var backgroundMusic: Music? = null
    
    fun playLineClickSound() {
        // Load from assets/sounds/click.wav if available
        try {
            val sound = loadSound("sounds/click.wav")
            sound?.play(soundVolume * masterVolume)
        } catch (e: Exception) {
            // Asset not found, continue without sound
        }
    }
    
    fun playGameOverSound() {
        try {
            val sound = loadSound("sounds/gameover.wav")
            sound?.play(soundVolume * masterVolume)
        } catch (e: Exception) {
            // Asset not found
        }
    }
    
    fun playLevelCompleteSound() {
        try {
            val sound = loadSound("sounds/levelcomplete.wav")
            sound?.play(soundVolume * masterVolume)
        } catch (e: Exception) {
            // Asset not found
        }
    }
    
    fun playBackgroundMusic(musicFile: String) {
        try {
            backgroundMusic?.stop()
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile))
            backgroundMusic?.isLooping = true
            backgroundMusic?.volume = musicVolume * masterVolume
            backgroundMusic?.play()
        } catch (e: Exception) {
            // Asset not found
        }
    }
    
    fun stopBackgroundMusic() {
        backgroundMusic?.stop()
    }
    
    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }
    
    fun setSoundVolume(volume: Float) {
        soundVolume = volume.coerceIn(0f, 1f)
    }
    
    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        backgroundMusic?.volume = musicVolume * masterVolume
    }
    
    private fun loadSound(path: String): Sound? {
        return try {
            if (!soundCache.containsKey(path)) {
                soundCache[path] = Gdx.audio.newSound(Gdx.files.internal(path))
            }
            soundCache[path]
        } catch (e: Exception) {
            null
        }
    }
    
    override fun dispose() {
        soundCache.values.forEach { it.dispose() }
        soundCache.clear()
        backgroundMusic?.dispose()
    }
}
