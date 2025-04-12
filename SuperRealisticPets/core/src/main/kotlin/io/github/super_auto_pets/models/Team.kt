package io.github.super_auto_pets.models

import io.github.super_auto_pets.interfaces.GameUnit

class Team {
    private var _teams: MutableList<GameUnit> = mutableListOf()
    var teams: MutableList<GameUnit>
        get() = _teams
        set(value) { _teams = value }


}
