package io.github.some_example_name

import com.badlogic.gdx.ApplicationAdapter

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class Main : ApplicationAdapter()

fun main() {

    val playerA = Player()
    val playerB = Player()

    val playerAController = PlayerController(playerA)
    val playerBController = PlayerController(playerB)

    playerAController.startTurn()
    playerBController.startTurn()

    while (true) {

        println("Player A's turn")

        while ( playerA.gold > 0 ) {

            println("Gold : ${playerA.gold}")

            println("Team :")
            for (i in playerA.team.teams.indices) {
                println(" ${playerA.team.teams[i].name}")
            }

            println("Shop :")
            for (i in playerA.shop.slots.indices) {
                println(" ${playerA.shop.slots[i].name}")
            }
            println("Shop frozen : ${playerA.shop.frozenUnits}")
            println("Chose a number between 0 to 3 to buy an animal from the shop")
            println("Chose 4 or 5 to buy an item from the shop")

            val input = readlnOrNull()?.toIntOrNull()

            if (input == null) {
                println("Invalid input. Please enter a number.")
                continue
            } else if (input == -1) {
                playerAController.endTurn()
                break
            } else if (input in 0..4){
                println("Chose a number between 0 to 4 to place an animal to your team")
                val targetPos = readlnOrNull()?.toIntOrNull()
                if (targetPos != null) {
                    playerAController.buy(input, targetPos)
                }
            } else if (input in 5..6) {
                println("Chose a number between 0 to 4 to place an item to an animal in your team")
                val targetPos = readlnOrNull()?.toIntOrNull()
                if (targetPos != null) {
                    playerAController.buy(input, targetPos)
                }
            } else {
            }
        }

        break

    }

}
