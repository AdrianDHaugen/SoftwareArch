package io.github.super_auto_pets.controller

import io.github.super_auto_pets.factories.ItemFactory
import io.github.super_auto_pets.factories.SpriteFactory
import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.models.Empty
import io.github.super_auto_pets.models.Item
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Sprite
import io.github.super_auto_pets.utilities.JsonParser

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
        player.gold -= 1
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
        // Create a deep copy if it's a sprite
        val unitToAdd = if (gameUnit is Sprite) {
            SpriteFactory.createCopy(gameUnit)
        } else {
            gameUnit
        }

        player.team.teams[targetPos] = unitToAdd
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
            else -> -1
        }
    }


    private fun buyTargetedItem(gameUnit: GameUnit, targetPos: Int): Int {
        if (player.team.teams[targetPos] is Empty) return -1

        val item = gameUnit as Item
        player.gold -= item.cost

        val sprite = player.team.teams[targetPos] as Sprite

        // Create a deep copy of the item when applying
        val itemCopy = ItemFactory.createCopy(item)
        sprite.item = itemCopy

        // Update the sprite's stats with the item's bonuses
        sprite.attack += item.addAttack
        sprite.health += item.addHealth

        player.shop.slots[player.shop.slots.indexOf(gameUnit)] = Empty()

        return 0
    }

    private fun generateEmptyTeamSlots() {
        repeat(4) { player.team.teams.add(Empty()) }
    }

    private fun generateInitialShop() {
        player.shop.slots.clear()
        repeat(4) { player.shop.slots.add(generateRandomSprite()) }  // Assuming 5 shop slots
        repeat(2) { player.shop.slots.add(generateRandomItem()) }
    }

    private fun generateShopSlot(pos : Int): GameUnit {
        return if (pos < 4) {
            generateRandomSprite()
        } else {
            generateRandomItem()
        }
    }

    private fun generateRandomSprite(): Sprite {
        // Get a random sprite from database and create a copy
        val originalSprite = spritesDB.random()
        return SpriteFactory.createCopy(originalSprite)
    }


    private fun generateRandomItem(): Item {
        // Get a random item from database and create a copy
        val originalItem = itemsDB.random()
        return ItemFactory.createCopy(originalItem)
    }

    fun endTurn() {
        //player.team.teams.forEach { it.onTurnStart() }
    }
}
