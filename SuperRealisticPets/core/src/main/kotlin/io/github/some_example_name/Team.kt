package io.github.some_example_name

class Team {
    private val teams: MutableList<GameUnit> = mutableListOf()

    operator fun get(pos: Int): GameUnit {
        return teams[pos]
    }

    operator fun set(pos: Int, value: GameUnit) {
        teams[pos] = value
    }

    fun move(rosterInit: Int, rosterFinal: Int, movedAnimal: GameUnit) {
        val temp = teams[rosterInit]
        teams[rosterInit] = teams[rosterFinal]
        teams[rosterFinal] = temp
    }

    fun hasSummonSpace(): Boolean {
        if (teams.size < 5) {
            return true
        }
        return false
    }

    fun summon(sprite: Sprite, pos: Int) {
        teams[pos] = sprite
    }

    fun size(): Int {
        return teams.size
    }
}
