package io.github.super_auto_pets.interfaces

interface GameUnit {
    var isFrozen: Boolean
    val name: String
    val cost: Int

    fun toggleFreeze() {
        isFrozen = !isFrozen
    }
}
