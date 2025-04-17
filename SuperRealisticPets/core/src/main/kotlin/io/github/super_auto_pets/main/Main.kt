package io.github.super_auto_pets.main

import com.badlogic.gdx.ApplicationAdapter
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Player

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class Main : ApplicationAdapter()

fun main() {
    val playerA = Player()
    val playerB = Player()

    val playerAController = PlayerController(playerA)
    val playerBController = PlayerController(playerB)

    // Start turns
    playerAController.startTurn()
    playerBController.startTurn()

    // Each player takes their turn
    handleTurn("Player A", playerA, playerAController)
    handleTurn("Player B", playerB, playerBController)

    val battle = Battle()
    battle.playerA = playerA
    battle.playerB = playerB

    // Start battle
    val battleController = BattleController()
    battleController.battle = battle
    battleController.startBattle()
}

fun handleTurn(name: String, player: Player, controller: PlayerController) {
    println("$name's turn")

    while (player.gold > 0) {
        println("Gold: ${player.gold}")

        println("Team:")
        for (i in player.team.teams.indices) {
            println(" ${player.team.teams[i].toString()}")
        }

        println("Shop:")
        for (i in player.shop.slots.indices) {
            println(" ${player.shop.slots[i].toString()}")
        }

        println("Shop frozen: ${player.shop.frozenUnits}")
        println("Choose a number between 0 to 4 to buy an animal from the shop")
        println("Choose 5 or 6 to buy an item from the shop")
        println("Enter -1 to end your turn")
        println("Enter 7 to sell an animal")
        println("Enter 8 to toggle freeze")
        println("Enter 9 to reroll")
        println("Enter 10 to move")
        println("Enter 11 to combine")

        val input = readlnOrNull()?.toIntOrNull()

        if (input == null) {
            println("Invalid input. Please enter a number.")
            continue
        } else if (input == -1) {
            controller.endTurn()
            break
        } else if (input in 0..4) {
            println("Choose a number between 0 to 3 to place an animal to your team")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null) {
                controller.buy(input, targetPos)
            }
        } else if (input in 5..6) {
            println("Choose a number between 0 to 3 to place an item to an animal in your team")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null) {
                controller.buy(input, targetPos)
            }
        } else if (input == 7) {
            println("Choose a number between 0 to 3 to sell an animal")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null) {
                controller.sell(targetPos)
            }
        } else if (input == 8) {
            println("Choose a number between 0 to 5 to toggle freeze")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null) {
                controller.toggleFreeze(targetPos)
            }
        } else if (input == 9) {
            controller.reroll()
        } else if (input == 10) {
            println("Choose a number between 0 to 3 to move an animal")
            val rosterInit = readlnOrNull()?.toIntOrNull()
            println("Choose a number between 0 to 3 to place the animal")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null && rosterInit != null) {
                controller.move(rosterInit, targetPos)
            }
        } else if (input == 11) {
            println("Choose the first animal to combine")
            val rosterInit = readlnOrNull()?.toIntOrNull()
            println("Choose the second animal to combine")
            val targetPos = readlnOrNull()?.toIntOrNull()
            if (targetPos != null && rosterInit != null) {
                controller.combine(rosterInit, targetPos)
            }
        }
    }
}

