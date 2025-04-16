package io.github.super_auto_pets.controller

import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Sprite

class BattleController(val battle: Battle = Battle()) {

    fun nextAttackStep(): AttackEvent? {
        // Check if both teams still have living sprites.
        val teamALive = battle.playerA.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }
        val teamBLive = battle.playerB.team.teams.filterIsInstance<Sprite>().any { it.health > 0 }
        if (!teamALive || !teamBLive) {
            println("Battle is over.")
            return null
        }

        // Choose the left team’s front fighter as the last alive
        // and the right team’s front fighter as the first alive.
        val spriteA = battle.playerA.team.teams.filterIsInstance<Sprite>().lastOrNull { it.health > 0 }
        val spriteB = battle.playerB.team.teams.filterIsInstance<Sprite>().firstOrNull { it.health > 0 }
        if (spriteA == null || spriteB == null) return null

        // Capture health before attack.
        val oldAHp = spriteA.health
        val oldBHp = spriteB.health

        // Simulate the attack.
        spriteB.health -= spriteA.attack
        spriteA.health -= spriteB.attack

        // Identify which sprites died in this round.
        val diedList = mutableListOf<Sprite>()
        if (spriteA.health <= 0) diedList.add(spriteA)
        if (spriteB.health <= 0) diedList.add(spriteB)

        // Remove dead sprites from each team.
        removeDead(battle.playerA.team.teams)
        removeDead(battle.playerB.team.teams)

        // Create and return the AttackEvent for this step.
        return AttackEvent(
            attacker = spriteA,
            defender = spriteB,
            oldAttackerHp = oldAHp,
            oldDefenderHp = oldBHp,
            newAttackerHp = spriteA.health,
            newDefenderHp = spriteB.health,
            diedSprites = diedList
        )
    }

    private fun removeDead(team: MutableList<GameUnit>) {
        val deadSprites = team.filterIsInstance<Sprite>().filter { it.health <= 0 }
        team.removeAll(deadSprites)

        // Log them
        for (sprite in deadSprites) {
            println("${sprite.name} is dead.")
        }
    }

    fun attack(sprite1: Sprite, sprite2: Sprite) {
        println("${sprite1.name} attacks ${sprite2.name}!")
        sprite2.health -= sprite1.attack
        sprite1.health -= sprite2.attack
    }
}

