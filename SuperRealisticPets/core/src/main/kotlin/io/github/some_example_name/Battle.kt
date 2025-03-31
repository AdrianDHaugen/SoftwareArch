package io.github.some_example_name

class Battle(
    private val playerA: Player,
    private val playerB: Player
) {

    fun startBattle() {
        println(" Battle starts!")
        playerA.team.teams.forEach { it.onBattleStart() }
        playerB.team.teams.forEach { it.onBattleStart() }

        while (playerA.team.teams.any { it.isAlive() } && playerB.team.teams.any { it.isAlive() }) {
            val a = playerA.team.teams.firstOrNull { it.isAlive() }
            val b = playerB.team.teams.firstOrNull { it.isAlive() }

            if (a == null || b == null) break

            println("\n ${a.name} vs ${b.name}")
            a.attack(b)

            removeDead(playerA.team.teams)
            removeDead(playerB.team.teams)
        }

        println("\n Battle result:")
        when {
            playerA.team.teams.any { it.isAlive() } && playerB.team.teams.none { it.isAlive() } -> println("Team A wins!")
            playerB.team.teams.any { it.isAlive() } && playerA.team.teams.none { it.isAlive() } -> println("Team B wins!")
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
