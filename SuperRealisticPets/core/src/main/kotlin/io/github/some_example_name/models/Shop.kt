package io.github.some_example_name.models

import io.github.some_example_name.interfaces.GameUnit

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
