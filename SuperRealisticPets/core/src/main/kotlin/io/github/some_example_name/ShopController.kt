package io.github.some_example_name

const val ERROR_BUY_TO_EMPTY = -1

class ShopController(private val player: Player) {

    fun toggleFreeze(pos: Int): Int {
        val shopSlot = player.shop.getSlot(pos)
        if (shopSlot is Empty) {
            return -1
        }

        player.shop.toggleFreeze(pos)
        return 0
    }

    fun reroll(): Int {
        if (player.gold < 1) return -1
        player.shop.reroll()
        return 0
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        val shopSlot = player.shop.getSlot(itemPos)
        if (shopSlot is Empty) {
            return -1
        }
        if (player.gold < shopSlot.cost) {
            return -1
        }
        return when (shopSlot) {
            is Sprite -> buySpriteResponse(shopSlot, targetPos)
            is Item -> buyItemResponse(shopSlot, targetPos)
            else -> -1
        }
    }

    private fun buySpriteResponse(gameUnit: GameUnit, targetPos: Int): Int {
        return when {
            player.team[targetPos] is Empty -> buyToEmptyResponse(gameUnit, targetPos)
            gameUnit::class == player.team[targetPos]::class -> buyToSameResponse(gameUnit, targetPos)
            else -> buyDifferentSpriteResponse(gameUnit, targetPos)
        } }

    private fun buyToEmptyResponse(gameUnit: GameUnit, targetPos: Int): Int {
        return ERROR_BUY_TO_EMPTY
    }

    private fun buyToSameResponse(gameUnit: GameUnit, targetPos: Int): Int {
        val targetUnit = player.team[targetPos]
        val target = Pair("team", targetPos)
        val item = gameUnit.buy() as GameUnit

        player.gold -= item.cost
        //targetUnit.increaseXp(1)

        return 0
    }

    private fun buyDifferentSpriteResponse(gameUnit: GameUnit, targetPos: Int): Int {
        if (!player.team.hasSummonSpace()) return -1

        val sprite = gameUnit as Sprite
        player.gold -= sprite.cost
        player.team.summon(sprite, targetPos)

        return 0
    }

    private fun buyItemResponse(gameUnit: GameUnit, targetPos: Int): Int {
        return buyTargetedItem(gameUnit, targetPos)
    }


    private fun buyTargetedItem(gameUnit: GameUnit, targetPos: Int): Int {
        if (player.team[targetPos] is Empty) return -1

        val item = gameUnit as Item
        val actor = Pair("team", targetPos)
        player.gold -= item.cost
        gameUnit.buy()

        return 0
    }

    private fun buyNonTargetedItem(gameUnit: GameUnit, targetPos: Int): Int {
        if (player.team.size() < 1) return -1

        player.gold -= gameUnit.cost
        gameUnit.buy()
        return 0
    }

    fun endTurn() {
    }
}
