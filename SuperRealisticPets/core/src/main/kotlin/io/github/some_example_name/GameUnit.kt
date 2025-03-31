package io.github.some_example_name

interface GameUnit {
    val name: String
    val cost: Int

    fun onTurnStart() {}
    fun onBattleStart() {}
    fun toggleFreeze()
    fun buy(): GameUnit {
        return this
    }
}
