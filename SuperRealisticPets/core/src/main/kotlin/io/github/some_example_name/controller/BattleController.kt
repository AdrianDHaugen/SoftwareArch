package io.github.some_example_name.controller

import io.github.some_example_name.interfaces.GameUnit
import io.github.some_example_name.models.Battle
import io.github.some_example_name.models.Sprite

class BattleController(private val battle: Battle = Battle() ) {

    fun startBattle() {
        println(" Battle starts!")

        while (battle.playerA.team.teams.filterIsInstance<Sprite>().any { it.health > 0 } && battle.playerB.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }) {
            val spriteA = battle.playerA.team.teams.filterIsInstance<Sprite>().firstOrNull { it.health > 0 }
            val spriteB = battle.playerB.team.teams.filterIsInstance<Sprite>().firstOrNull { it.health > 0 }

            if (spriteA == null || spriteB == null) break

            println("\n ${spriteA.name} vs ${spriteB.name}")
            attack(spriteA, spriteB)

            removeDead(battle.playerA.team.teams)
            removeDead(battle.playerB.team.teams)
        }

        println("\n Battle result:")
        when {
            battle.playerA.team.teams.filterIsInstance<Sprite>().any { it.health > 0 } && battle.playerB.team.teams.filterIsInstance<Sprite>().none { it.health > 0 } -> println("Team A wins!")
            battle.playerB.team.teams.filterIsInstance<Sprite>().any { it.health > 0 } && battle.playerA.team.teams.filterIsInstance<Sprite>().none { it.health > 0 } -> println("Team B wins!")
            else -> println("Draw!")
        }
    }

    fun attack(sprite1: Sprite, sprite2: Sprite) {
        println("$sprite1 attacks $sprite2!")
        sprite2.health -= sprite1.attack
        sprite1.health -= sprite2.attack
        //sprite1.item?.onAttack(sprite1, sprite2)
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
