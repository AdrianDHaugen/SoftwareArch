import io.github.some_example_name.GameUnit
import io.github.some_example_name.Item
import io.github.some_example_name.sprite.Sprite
import io.github.some_example_name.Empty

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
        if (player.gold < shopSlot.item.cost) {
            return -1
        }
        return when (shopSlot.item) {
            is Sprite -> buySpriteResponse(shopSlot, targetPos)
            is Item -> buyItemResponse(shopSlot, targetPos)
            else -> -1
        }
    }

    private fun buySpriteResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return when {
            player.team.sprite[targetPos] is Empty -> buyToEmptyResponse(shopSlot, targetPos)
            shopSlot.item::class == player.team[targetPos]::class -> buyToSameResponse(shopSlot, targetPos)
            else -> buyDifferentSpriteResponse(shopSlot, targetPos)
        } }

    private fun buyToEmptyResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return ERROR_BUY_TO_EMPTY
    }

    private fun buyToSameResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        val targetUnit = player.team[targetPos]
        val target = Pair("team", targetPos)
        val item = shopSlot.buy() as GameUnit

        player.gold -= item.cost
        targetUnit.increaseXp(1)

        return 0
    }

    private fun buyDifferentSpriteResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        if (!player.team.hasSummonSpace) return -1

        val sprite = shopSlot.sprite as Sprite
        player.gold -= sprite.cost
        player.team.summon(sprite, targetPos)

        return 0
    }

    private fun buyItemResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return if ((shopSlot.item as Item).isTargeted) {
            buyTargetedItem(shopSlot, targetPos)
        } else {
            buyNonTargetedItem(shopSlot, targetPos)
        }
    }


    private fun buyTargetedItem(shopSlot: ShopSlot, targetPos: Int): Int {
        if (player.team[targetPos] is Empty) return -1

        val item = shopSlot.item as Item
        val actor = Pair("team", targetPos)
        player.gold -= item.cost
        shopSlot.buy()

        return 0
    }

    private fun buyNonTargetedItem(shopSlot: ShopSlot, targetPos: Int): Int {
        if (player.team.size < 1) return -1
        val item = shopSlot.item

        player.gold -= item.cost
        shopSlot.buy()
        return 0
    }

    fun endTurn() {
    }
}
