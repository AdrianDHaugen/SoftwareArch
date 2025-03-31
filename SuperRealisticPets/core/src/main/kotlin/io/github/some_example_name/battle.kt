package io.github.some_example_name.game

import io.github.some_example_name.Sprite

class Battle(
    private val teamA: MutableList<Sprite>,
    private val teamB: MutableList<Sprite>
) {

    fun startBattle() {
        println(" Battle starts!")
        teamA.forEach { it.onBattleStart() }
        teamB.forEach { it.onBattleStart() }

        while (teamA.any { it.isAlive() } && teamB.any { it.isAlive() }) {
            val a = teamA.firstOrNull { it.isAlive() }
            val b = teamB.firstOrNull { it.isAlive() }

            if (a == null || b == null) break

            println("\n ${a.name} vs ${b.name}")
            a.attack(b)

            removeDead(teamA)
            removeDead(teamB)
        }

        println("\n Battle result:")
        when {
            teamA.any { it.isAlive() } && teamB.none { it.isAlive() } -> println("Team A wins!")
            teamB.any { it.isAlive() } && teamA.none { it.isAlive() } -> println("Team B wins!")
            else -> println("Draw!")
        }
    }

    private fun removeDead(team: MutableList<Sprite>) {
        val dead = team.filter { !it.isAlive() }
        dead.forEach {
            println(" ${it.name} has fainted.")
        }
    }
}
