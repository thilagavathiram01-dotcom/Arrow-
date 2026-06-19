package com.arrows.game

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

fun main() {
    val config = Lwjgl3ApplicationConfiguration()
    config.setTitle("Arrows Game")
    config.setWindowedMode(1080, 1920)
    config.useVsync(true)
    
    Lwjgl3Application(ArrowsGame(), config)
}
