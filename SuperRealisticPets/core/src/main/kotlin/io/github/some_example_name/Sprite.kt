package io.github.some_example_name

open class Sprite(
    override val name: String,
    var attack: Int,
    var health: Int,
    var tier: Int,
    var item: Item? = null,
    override var cost: Int,
): GameUnit {
    override fun onTurnStart() {
        item?.onTurnStart(this)
    }

    override fun onBattleStart() {
        item?.onBattleStart(this)
    }

    override fun toggleFreeze() {
        TODO("Not yet implemented")
    }

    override fun buy(): Any {
        TODO("Not yet implemented")
    }

    open fun attack(target: Sprite) {
        println("$name attacks ${target.name}!")
        target.health -= this.attack
        this.health -= target.attack
        item?.onAttack(this, target)
    }

    fun isAlive(): Boolean = health > 0
}
