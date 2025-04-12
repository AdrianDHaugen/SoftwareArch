package io.github.some_example_name.interfaces

interface GameUnit {
    var isFrozen: Boolean
    val name: String
    val cost: Int

    fun toggleFreeze() {
        isFrozen = !isFrozen
    }
}
