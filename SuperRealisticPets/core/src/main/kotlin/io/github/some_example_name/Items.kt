package io.github.some_example_name

open class Item(
    override val name: String,
    override val cost: Int,
): GameUnit {
    open fun onTurnStart(holder: Sprite) {}
    open fun onBattleStart(holder: Sprite) {}
    open fun onAttack(holder: Sprite, target: Sprite) {}
    override fun toggleFreeze() { }
    override fun buy(): Any {
        TODO("Not yet implemented")
    }
}
