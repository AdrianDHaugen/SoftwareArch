package io.github.some_example_name

class Player(val name: String) {
    var gold: Int = 10
    var turn: Int = 1
    val team = Team()
    val shop = Shop()
    private val shopController = ShopController(this)


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

        if (sprite is Sprite) {
            gold += sprite.level
        }

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
        val sprite1 = team[rosterInit]
        val sprite2 = team[rosterFinal]

        if (sprite1 is Empty || sprite2 is Empty || sprite1::class != sprite2::class) {
            return -1
        }

        if (sprite1 is Sprite && sprite2 is Sprite) {
            sprite1.mergeStats(sprite1, sprite2)
        }
        //anim1.xp = 0
        team[rosterInit] = Empty()

        return 0
    }

    fun endTurn() {
    }
}
