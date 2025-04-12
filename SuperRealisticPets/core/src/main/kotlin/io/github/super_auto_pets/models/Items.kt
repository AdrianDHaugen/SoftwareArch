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

}
