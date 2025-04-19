package io.github.super_auto_pets.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.super_auto_pets.interfaces.HighscoreService

class FirebaseHighscoreService : HighscoreService {
    override fun updateHighscore(playerName: String, winStreak: Int) {
        val db = Firebase.firestore
        val doc = db.collection("highscores").document(playerName)

        doc.get().addOnSuccessListener {
            val best = it.getLong("bestStreak") ?: 0
            val newBest = maxOf(winStreak, best.toInt())
            val entry = HighscoreEntry(playerName, winStreak, newBest)
            doc.set(entry)
        }
    }
}
