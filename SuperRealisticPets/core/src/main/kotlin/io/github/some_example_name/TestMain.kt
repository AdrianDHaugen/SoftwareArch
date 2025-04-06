package io.github.some_example_name

fun main() {
    val teamA = mutableListOf(
        Sprite("Ant", 2, 1, 1, null,1,1 ),
        Sprite("Fish", 3, 10, 1, null, 1, 1)
    )

    val teamB = mutableListOf(
        Sprite("Otter", 2, 1, 1, null, 1,1),
        Sprite("Beaver", 2, 2, 1, null, 1 ,1)
    )

    val battle = Battle(teamA, teamB)
    battle.startBattle()
}
