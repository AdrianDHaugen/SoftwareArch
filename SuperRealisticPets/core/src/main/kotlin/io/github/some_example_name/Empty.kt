package io.github.some_example_name

open class Empty():GameUnit {

    override val name: String = "Default Name"
    override val cost: Int = 100


    override fun onTurnStart() {}
    override fun onBattleStart() {}
    override fun toggleFreeze() {
        TODO("Not yet implemented")
    }

    override fun buy(): GameUnit {
        return this
    }
}
