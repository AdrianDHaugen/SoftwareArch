package io.github.some_example_name

open class Sprite(
    override val name: String,
    var attack: Int,
    var health: Int,
    var tier: Int,
    var item: Item? = null,
    var level: Int = 1,
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

    open fun onFaint(allies: MutableList<Sprite>, enemies: MutableList<Sprite>) {}

    open fun attack(target: Sprite) {
        println("$name attacks ${target.name}!")
        target.health -= this.attack
        this.health -= target.attack
        item?.onAttack(this, target)
    }

    fun mergeStats(sprite1: Sprite, sprite2: Sprite) {
        sprite1.attack += sprite2.attack
        sprite1.health += sprite2.health
        sprite1.tier += sprite2.tier
        sprite1.level += sprite2.level
    }

    fun isAlive(): Boolean = health > 0

    fun copy(): Sprite {
        return Sprite(name, attack, health, tier, item, level ,cost)
    }
}





