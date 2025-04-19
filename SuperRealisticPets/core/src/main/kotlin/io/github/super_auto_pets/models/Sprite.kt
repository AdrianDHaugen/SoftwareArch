package io.github.super_auto_pets.models

import io.github.super_auto_pets.interfaces.ISprite

class Sprite : ISprite {

    private var _name: String = name
    override var name: String
        get() = _name
        set(value) { _name = value }

    private var _attack: Int = attack
    override var attack: Int
        get() = _attack
        set(value) { _attack = value }

    private var _health: Int = health
    override var health: Int
        get() = _health
        set(value) { _health = value }

    private var _tier: Int = tier
    override var tier: Int
        get() = _tier
        set(value) { _tier = value }

    private var _item: Item? = item
    override var item: Item?
        get() = _item
        set(value) { _item = value }

    private var _level: Int = level
    override var level: Int
        get() = _level
        set(value) { _level = value }

    private var _cost: Int = cost
    override var cost: Int
        get() = _cost
        set(value) { _cost = value }

    private var _isFrozen: Boolean = isFrozen
    override var isFrozen: Boolean
        get() = _isFrozen
        set(value) { _isFrozen = value }

    private var _color: String = color
    var color: String
        get() = _color
        set(value) { _color = value }

    override fun toString(): String {
        return "Sprite(name='$name', attack=$attack, health=$health, tier=$tier, item=$item, level=$level, cost=$cost, isFrozen=$isFrozen, color='$color')"
    }
}
