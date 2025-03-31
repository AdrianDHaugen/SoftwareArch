package io.github.some_example_name

open class Sprite(
    val name: String,
    var attack: Int,
    var health: Int,
    var tier: Int,
    var item: Item? = null
) {
    open fun onTurnStart() {
        item?.onTurnStart(this)
    }

    open fun onBattleStart() {
        item?.onBattleStart(this)
    }

    open fun attack(target: Sprite) {
        println("$name attacks ${target.name}!")
        target.health -= this.attack
        this.health -= target.attack
        item?.onAttack(this, target)
    }

    fun isAlive(): Boolean = health > 0
}
