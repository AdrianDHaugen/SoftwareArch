package io.github.super_auto_pets.models

import io.github.super_auto_pets.interfaces.GameUnit

class Item : GameUnit {

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

    private var _addHealth: Int = addHealth
    var addHealth: Int
        get() = _addHealth
        set(value) { _addHealth = value }

    private var _addAttack: Int = addAttack
    var addAttack: Int
        get() = _addAttack
        set(value) { _addAttack = value }

    private var _path: String = path
    override var path : String
        get() = _path
        set(value) { _path = value }

    override fun toString(): String {
        return "Item(name='$name', cost=$cost, isFrozen=$isFrozen, addHealth=$addHealth, addAttack=$addAttack, path='$path's)"
    }
}
