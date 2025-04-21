package io.github.super_auto_pets.interfaces

interface GameUnit {
    var isFrozen: Boolean
    val name: String
    val cost: Int
    val path: String

    fun toggleFreeze() {
        isFrozen = !isFrozen
    }
}
