package io.github.some_example_name

const val ERROR_BUY_TO_EMPTY = -1

class ShopController(private val player: Player) {

    init {
        generateInitialShop()
        generateEmptyTeamSlots()
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
                newSlots.add(generateShopSlot())  // Replace others
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
            player.team.teams[targetPos] is Empty -> buyToEmptyResponse()
            gameUnit::class == player.team.teams[targetPos]::class -> buyToSameResponse(gameUnit, targetPos)
            else -> buyDifferentSpriteResponse(gameUnit, targetPos)
        } }

    private fun buyToEmptyResponse(): Int {
        return ERROR_BUY_TO_EMPTY
    }

    private fun buyToSameResponse(gameUnit: GameUnit, targetPos: Int): Int {
        val targetUnit = player.team.teams[targetPos]

        player.gold -= gameUnit.cost
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
        if (player.team.teams[targetPos] is Empty) return -1

        val item = gameUnit as Item
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

    private fun generateEmptyTeamSlots() {
        repeat(4) { player.team.teams.add(Empty()) }
    }

    private fun generateInitialShop() {
        player.shop.slots.clear()
        repeat(5) { player.shop.slots.add(generateRandomAnimal()) }  // Assuming 5 shop slots
        repeat(2) { player.shop.slots.add(generateRandomEquipment()) }
    }

    private fun generateShopSlot(): GameUnit {
        // Generate a random item (Animal or Equipment)
        val item = if ((0..1).random() == 0) generateRandomAnimal() else generateRandomEquipment()
        return item
    }

    private fun generateRandomAnimal(): Sprite {
        return Sprite("Animal ${(1..10).random()}", 1, 1, 3, null, 1, 1)  // Example random animal. Here we have to return the animal object. The values after comma are for health, attack and level
    }

    private fun generateRandomEquipment(): Item {
        return Item("Equipment ${(1..5).random()}", 1)  // Example random equipment
    }

    fun endTurn() {
    }
}
