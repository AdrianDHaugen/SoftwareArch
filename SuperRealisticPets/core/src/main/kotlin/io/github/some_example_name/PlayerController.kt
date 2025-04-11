package io.github.some_example_name

class PlayerController(private val player: Player) {

    fun startTurn() {
        player.turn++
        player.gold = 10
        reroll()
    }

    fun toggleFreeze(pos: Int): Int {
        return player.shopController.toggleFreeze(pos)
    }

    fun reroll(): Int {
        return player.shopController.reroll()
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        return player.shopController.buy(itemPos, targetPos)
    }

    fun sell(pos: Int): Int {
        val sprite = player.team.teams[pos]
        if (sprite is Empty) {
            return -1
        }

        if (sprite is Sprite) {
            player.gold += sprite.level
        }

        player.team.teams[pos] = Empty()
        return 0
    }

    fun move(rosterInit: Int, rosterFinal: Int): Int {
        if (player.team.teams[rosterInit] is Empty || rosterInit == rosterFinal) {
            return -1
        }
        val movedAnimal = player.team.teams[rosterInit]
        player.team.teams[rosterInit] = Empty()
        player.team.move(rosterInit, rosterFinal, movedAnimal)
        return 0
    }

    fun combine(rosterInit: Int, rosterFinal: Int): Int {
        val sprite1 = player.team.teams[rosterInit]
        val sprite2 = player.team.teams[rosterFinal]

        if (sprite1 is Empty || sprite2 is Empty || sprite1::class != sprite2::class) {
            return -1
        }

        if (sprite1 is Sprite && sprite2 is Sprite) {
            sprite1.mergeStats(sprite1, sprite2)
        }
        //anim1.xp = 0
        player.team.teams[rosterInit] = Empty()

        return 0
    }

    fun endTurn() {
        player.shopController.endTurn()
    }
}
