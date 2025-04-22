package io.github.super_auto_pets.firebase

data class HighscoreEntry(
    val playerName: String = "",
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)

