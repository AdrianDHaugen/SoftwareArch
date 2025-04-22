package io.github.super_auto_pets.firebase

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query

object HighscoreManager {

    private val db = Firebase.firestore

    fun updateHighscore(playerName: String, winStreak: Int) {
        val docRef = db.collection("highscores").document(playerName)

        docRef.get().addOnSuccessListener { doc ->
            val currentBest = doc.getLong("bestStreak") ?: 0
            val newBest = maxOf(winStreak, currentBest.toInt())

            val entry = HighscoreEntry(
                playerName = playerName,
                currentStreak = winStreak,
                bestStreak = newBest
            )

            docRef.set(entry)
        }
    }

    fun fetchTopHighscores(limit: Long = 10, onResult: (List<HighscoreEntry>) -> Unit) {
        db.collection("highscores")
            .orderBy("bestStreak", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val scores = result.documents.mapNotNull { it.toObject(HighscoreEntry::class.java) }
                onResult(scores)
            }
    }
}

