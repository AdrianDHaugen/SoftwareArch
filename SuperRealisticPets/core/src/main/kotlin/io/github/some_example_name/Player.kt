package io.github.some_example_name

class Player(val name: String) {
    var gold: Int = 10
    var turn: Int = 1
    val team = Team()
    val shop = Shop()
    val shopController = ShopController(this)


    fun startTurn() {
        turn++
        gold = 10
        shop.startTurn()
    }

    fun toggleFreeze(pos: Int): Int {
        return shopController.toggleFreeze(pos)
    }

    fun reroll(): Int {
        return shopController.reroll()
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        return shopController.buy(itemPos, targetPos)
    }

    fun sell(pos: Int): Int {
        val sprite = team[pos]
        if (sprite is Empty) {
            return -1
        }

        gold += sprite.level
        team[pos] = Empty()
        return 0
    }

    fun move(rosterInit: Int, rosterFinal: Int): Int {
        if (team[rosterInit] is Empty || rosterInit == rosterFinal) {
            return -1
        }
        val movedAnimal = team[rosterInit]
        team[rosterInit] = Empty()
        team.move(rosterInit, rosterFinal, movedAnimal)
        return 0
    }

    fun combine(rosterInit: Int, rosterFinal: Int): Int {
        val anim1 = team[rosterInit]
        val anim2 = team[rosterFinal]

        if (anim1 is Empty || anim2 is Empty || anim1::class != anim2::class) {
            return -1
        }

        anim2.mergeStats(anim1)
        //anim1.xp = 0
        team[rosterInit] = Empty()

        return 0
    }

    fun endTurn() {
    }
}
