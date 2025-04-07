package io.github.some_example_name

class Team {
    private var _teams: MutableList<GameUnit> = mutableListOf()
    var teams: MutableList<GameUnit>
        get() = _teams
        set(value) { _teams = value }

    fun move(rosterInit: Int, rosterFinal: Int, movedAnimal: GameUnit) {
        val temp = teams[rosterInit]
        teams[rosterInit] = teams[rosterFinal]
        teams[rosterFinal] = temp
    }

    fun hasSummonSpace(): Boolean {
        return teams.size < 5
    }

    fun summon(sprite: Sprite, pos: Int) {
        teams[pos] = sprite
    }

    fun size(): Int {
        return teams.size
    }
}
