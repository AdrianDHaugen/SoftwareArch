package io.github.super_auto_pets.firebase

import com.badlogic.gdx.Gdx
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query

object HighscoreManager {

    private val db = Firebase.firestore

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

    fun appendWin(playerName: String) {
        val db = Firebase.firestore
        val highscoresRef = db.collection("highscores").document(playerName)

        highscoresRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val current = doc.getLong("bestStreak") ?: 0L
                val currentStreak = doc.getLong("currentStreak") ?: 0L
                highscoresRef.update(
                    mapOf(
                        "bestStreak" to current + 1,
                        "playerName" to playerName,
                        "currentStreak" to currentStreak + 1
                    )
                )
            } else {
                val newScore = hashMapOf(
                    "playerName" to playerName,
                    "bestStreak" to 1,
                    "currentStreak" to 1
                )
                highscoresRef.set(newScore)
            }
        }.addOnFailureListener {
            Gdx.app.log("Firebase", "Failed to update score for $playerName: ${it.message}")
        }
    }

    fun getCurrentStreak(playerName: String, callback: (Int) -> Unit) {
        val db = Firebase.firestore
        val highscoresRef = db.collection("highscores").document(playerName)

        highscoresRef.get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val current = doc.getLong("currentStreak")?.toInt() ?: 0
                    callback(current)
                } else {
                    callback(0) // Player not found yet = streak is 0
                }
            }
            .addOnFailureListener {
                Gdx.app.log("Firebase", "Failed to fetch streak for $playerName: ${it.message}")
                callback(0) // Also return 0 on error
            }
    }

    fun resetStreak(playerName: String) {
        val db = Firebase.firestore
        val highscoresRef = db.collection("highscores").document(playerName)

        highscoresRef.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                highscoresRef.update("currentStreak", 0)
            } else {
                highscoresRef.set(hashMapOf("currentStreak" to 0))
            }
        }.addOnFailureListener {
            Gdx.app.log("Firebase", "Failed to reset score for $playerName: ${it.message}")
        }
    }




}

