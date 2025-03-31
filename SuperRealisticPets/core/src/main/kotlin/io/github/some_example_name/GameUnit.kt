package io.github.some_example_name

interface GameUnit {
    var isFrozen: Boolean
    val name: String
    val cost: Int

    fun toggleFreeze() {
        isFrozen = !isFrozen
    }


    fun onTurnStart() {}
    fun onBattleStart() {}
}
