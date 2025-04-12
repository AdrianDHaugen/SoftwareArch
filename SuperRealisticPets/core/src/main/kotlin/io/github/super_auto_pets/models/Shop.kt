package io.github.super_auto_pets.models

import io.github.super_auto_pets.interfaces.GameUnit

class Shop {
    private var _slots: MutableList<GameUnit> = mutableListOf()
    var slots: MutableList<GameUnit>
        get() = _slots
        set(value) { _slots = value }

    private var _frozenUnits: MutableSet<Int> = mutableSetOf()
    var frozenUnits: MutableSet<Int>
        get() = _frozenUnits
        set(value) { _frozenUnits = value }
}
