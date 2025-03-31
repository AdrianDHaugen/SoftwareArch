package io.github.some_example_name

open class Empty():GameUnit {

    override val name: String = "Default Name"
    override val cost: Int = 100
    override var isFrozen: Boolean = false


    override fun onTurnStart() {}
    override fun onBattleStart() {}

}
