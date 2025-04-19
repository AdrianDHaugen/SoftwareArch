package io.github.super_auto_pets.interfaces

interface HighscoreService {
    fun updateHighscore(playerName: String, winStreak: Int)
}
