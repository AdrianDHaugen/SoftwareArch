package io.github.some_example_name

class Battle(
    private val playerA: Player,
    private val playerB: Player
) {

    fun startBattle() {
        println(" Battle starts!")
        //playerA.team.teams.forEach { it.onBattleStart() }
        //playerB.team.teams.forEach { it.onBattleStart() }

        while (playerA.team.teams.filterIsInstance<Sprite>().any { it.isAlive() } && playerB.team.teams.filterIsInstance<Sprite>().any { it.isAlive() }) {
            val a = playerA.team.teams.filterIsInstance<Sprite>().firstOrNull { it.isAlive() }
            val b = playerB.team.teams.filterIsInstance<Sprite>().firstOrNull { it.isAlive() }

            if (a == null || b == null) break

            println("\n ${a.name} vs ${b.name}")
            a.attack(b)

            removeDead(playerA.team.teams)
            removeDead(playerB.team.teams)
        }

        println("\n Battle result:")
        when {
            playerA.team.teams.filterIsInstance<Sprite>().any { it.isAlive() } && playerB.team.teams.filterIsInstance<Sprite>().none { it.isAlive() } -> println("Team A wins!")
            playerB.team.teams.filterIsInstance<Sprite>().any { it.isAlive() } && playerA.team.teams.filterIsInstance<Sprite>().none { it.isAlive() } -> println("Team B wins!")
            else -> println("Draw!")
        }
    }

    private fun removeDead(team: MutableList<GameUnit>) {
        team.filterIsInstance<Sprite>().forEach { sprite ->
            if (sprite.health > 0) {
                println("${sprite.name} is alive with ${sprite.health} health.")
            } else {
                team.remove(sprite)
                println("${sprite.name} is dead.")
            }
        }
    }
}
