package io.github.some_example_name


fun main() {
    println("🐾 Welcome to Super Realistic Pets - Text UI\n")

    val availableUnits = listOf(
        Sprite("Ant", 2, 1, 1, cost = 3),
        Sprite("Fish", 3, 2, 1, cost = 3),
        Sprite("Otter", 1, 2, 1, cost = 3),
        Sprite("Beaver", 2, 2, 1, cost = 3)
    )


    val teamA = createTeam("Team A", availableUnits)
    val teamB = createTeam("Team B", availableUnits)

    println("\n🆚 Let the battle begin!")
    val battle = Battle(teamA, teamB)
    battle.startBattle()
}

fun createTeam(name: String, unitPool: List<Sprite>): MutableList<Sprite> {
    val team = mutableListOf<Sprite>()

    println("📦 Build $name (max 5 units)")
    while (team.size < 5) {
        println("\nChoose a unit to add:")

        unitPool.forEachIndexed { index, sprite ->
            println("${index + 1}: ${sprite.name} (ATK: ${sprite.attack}, HP: ${sprite.health})")
        }
        println("0: Done")

        print("> ")
        val input = readlnOrNull()

        val choice = input?.toIntOrNull()
        if (choice == null) {
            println("❌ Invalid input. Try again.")
            continue
        }

        when {
            choice == 0 -> break
            choice in 1..unitPool.size -> {
                // Make a fresh copy of the unit
                val unit = unitPool[choice - 1].copy()
                team.add(unit)
                println("✅ Added ${unit.name} to $name.")
            }
            else -> println("❌ Invalid option.")
        }
    }

    println("✅ $name ready with ${team.size} units.")
    return team
}
