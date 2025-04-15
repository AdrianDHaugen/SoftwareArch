package io.github.super_auto_pets.controller

import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Sprite

class BattleController(val battle: Battle = Battle() ) {

    fun startBattle(): List<AttackEvent> {
        // Store each attack in a list
        val events = mutableListOf<AttackEvent>()

        println("Battle starts!")

        // This while loop finishes instantly, building up all attacks in 'events'.
        while (battle.playerA.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }
            && battle.playerB.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }) {

            val spriteA = battle.playerA.team.teams.filterIsInstance<Sprite>().firstOrNull { it.health > 0 }
            val spriteB = battle.playerB.team.teams.filterIsInstance<Sprite>().firstOrNull { it.health > 0 }

            if (spriteA == null || spriteB == null) break

            val oldAHp = spriteA.health
            val oldBHp = spriteB.health

            // Attack: spriteA hits spriteB, spriteB hits spriteA
            spriteB.health -= spriteA.attack
            spriteA.health -= spriteB.attack

            // Figure out who died
            val diedList = mutableListOf<Sprite>()
            if (spriteA.health <= 0) diedList.add(spriteA)
            if (spriteB.health <= 0) diedList.add(spriteB)

            removeDead(battle.playerA.team.teams)
            removeDead(battle.playerB.team.teams)

            // Record the event
            val event = AttackEvent(
                attacker = spriteA,
                defender = spriteB,
                oldAttackerHp = oldAHp,
                oldDefenderHp = oldBHp,
                newAttackerHp = spriteA.health,
                newDefenderHp = spriteB.health,
                diedSprites = diedList
            )
            events.add(event)
        }

        // After the loop, figure out final result
        println("Battle result:")
        val aliveA = battle.playerA.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }
        val aliveB = battle.playerB.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }
        when {
            aliveA && !aliveB -> println("Team A wins!")
            aliveB && !aliveA -> println("Team B wins!")
            else -> println("Draw!")
        }

        return events
    }

    // The old removeDead
    private fun removeDead(team: MutableList<GameUnit>) {
        val deadSprites = team.filterIsInstance<Sprite>().filter { it.health <= 0 }
        team.removeAll(deadSprites)

        // Log them
        for (sprite in deadSprites) {
            println("${sprite.name} is dead.")
        }
    }

    // You can keep this if you need to do a single Attack outside startBattle
    fun attack(sprite1: Sprite, sprite2: Sprite) {
        println("${sprite1.name} attacks ${sprite2.name}!")
        sprite2.health -= sprite1.attack
        sprite1.health -= sprite2.attack
    }
}
