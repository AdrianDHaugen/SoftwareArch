package io.github.some_example_name.controller

import io.github.some_example_name.interfaces.GameUnit
import io.github.some_example_name.models.Empty
import io.github.some_example_name.models.Item
import io.github.some_example_name.models.Player
import io.github.some_example_name.models.Sprite
import io.github.some_example_name.utilities.JsonParser

const val ERROR_BUY_TO_OCCUPIED = -1
var spritesDB: List<Sprite> = emptyList()
var itemsDB: List<Item> = emptyList()

class ShopController(private val player: Player) {

    init {
        parseGameUnits()
        generateInitialShop()
        generateEmptyTeamSlots()
    }

    private fun parseGameUnits() {
        spritesDB = JsonParser().parseSprites()
        itemsDB = JsonParser().parseItems()
    }

    fun toggleFreeze(pos: Int): Int {
        if (pos !in player.shop.slots.indices || player.shop.slots[pos] is Empty) {
            return -1
        }

        if (pos in player.shop.frozenUnits) {
            player.shop.frozenUnits.remove(pos)
        } else {
            player.shop.frozenUnits.add(pos)
        }

        player.shop.slots[pos].toggleFreeze()
        return 0
    }

    fun reroll(): Int {
        if (player.gold < 1) return -1

        val newSlots = mutableListOf<GameUnit>()
        for (i in player.shop.slots.indices) {
            if (i in player.shop.frozenUnits) {
                newSlots.add(player.shop.slots[i])  // Keep frozen items
            } else {
                newSlots.add(generateShopSlot(i))  // Replace others
            }
        }
        player.shop.slots.clear()
        player.shop.slots.addAll(newSlots)

        return 0
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        val shopSlot = player.shop.slots[itemPos]
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
            player.team.teams[targetPos] is Empty -> buyToEmptyResponse(gameUnit, targetPos)
            gameUnit::class == player.team.teams[targetPos]::class -> buyToSameResponse(gameUnit, targetPos)
            player.team.teams[targetPos] is Sprite -> buyToOccupiedSlotResponse()
            else -> return -1
        } }

    private fun buyToEmptyResponse(gameUnit: GameUnit, targetPos: Int): Int {
        player.team.teams[targetPos] = gameUnit
        player.gold -= gameUnit.cost

        player.shop.slots[player.shop.slots.indexOf(gameUnit)] = Empty()

        return 1
    }

    private fun buyToOccupiedSlotResponse() : Int {
        return ERROR_BUY_TO_OCCUPIED
    }

    private fun buyToSameResponse(gameUnit: GameUnit, targetPos: Int): Int {
        val targetUnit = player.team.teams[targetPos] as Sprite

        player.gold -= gameUnit.cost
        targetUnit.attack += 1
        targetUnit.health += 1

        //targetUnit.increaseXp(1)
        player.shop.slots[player.shop.slots.indexOf(gameUnit)] = Empty()

        return 0
    }

    private fun buyItemResponse(gameUnit: GameUnit, targetPos: Int): Int {
        return when {
            player.team.teams[targetPos] is Sprite -> buyTargetedItem(gameUnit, targetPos)
            else -> buyNonTargetedItem(gameUnit, targetPos)
        }
    }


    private fun buyTargetedItem(gameUnit: GameUnit, targetPos: Int): Int {
        if (player.team.teams[targetPos] is Empty) return -1

        val item = gameUnit as Item
        player.gold -= item.cost

        val sprite = player.team.teams[targetPos] as Sprite
        sprite.item = item

        player.shop.slots[player.shop.slots.indexOf(gameUnit)] = Empty()

        return 0
    }

    private fun buyNonTargetedItem(gameUnit: GameUnit, targetPos: Int): Int {
        if (player.team.teams.size < 1) return -1

        player.gold -= gameUnit.cost
        //gameUnit.buy()
        return 0
    }

    private fun generateEmptyTeamSlots() {
        repeat(4) { player.team.teams.add(Empty()) }
    }

    private fun generateInitialShop() {
        player.shop.slots.clear()
        repeat(5) { player.shop.slots.add(generateRandomSprite()) }  // Assuming 5 shop slots
        repeat(2) { player.shop.slots.add(generateRandomItem()) }
    }

    private fun generateShopSlot(pos : Int): GameUnit {
        return if (pos < 5) {
            generateRandomSprite()
        } else {
            generateRandomItem()
        }
    }

    private fun generateRandomSprite(): Sprite {
        val sprite = spritesDB.random()

        return sprite
    }


    private fun generateRandomItem(): Item {
        val item = itemsDB.random()

        return item
    }

    fun endTurn() {
        //player.team.teams.forEach { it.onTurnStart() }
    }
}
