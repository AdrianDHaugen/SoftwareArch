package io.github.some_example_name

open class Item(
    name: String,
    cost: Int,
    isFrozen: Boolean = false
) : GameUnit {

    private var _name: String = name
    override var name: String
        get() = _name
        set(value) { _name = value }

    private var _cost: Int = cost
    override var cost: Int
        get() = _cost
        set(value) { _cost = value }

    private var _isFrozen: Boolean = isFrozen
    override var isFrozen: Boolean
        get() = _isFrozen
        set(value) { _isFrozen = value }

    open fun onTurnStart(holder: Sprite) {}
    open fun onBattleStart(holder: Sprite) {}
    open fun onAttack(holder: Sprite, target: Sprite) {}
}
