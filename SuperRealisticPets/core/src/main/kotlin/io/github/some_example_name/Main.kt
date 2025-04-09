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
            println("Team : ${playerA.team.teams}")
            println("Shop : ${playerA.shop.slots}")
            println("Shop frozen : ${playerA.shop.frozenUnits}")
            println("Chose a number between 0 to 4 to buy an animal from the shop")
            println("Chose 5 or 6 to buy an item from the shop")

            var input = readlnOrNull()?.toIntOrNull()

            if (input == null) {
                println("Invalid input. Please enter a number.")
                continue
            } else if (input == -1) {
                playerAController.endTurn()
                break
            } else {
                playerAController.buy(input, 0)
            }
        }

    }

}
