package io.github.some_example_name

class Shop {
    private val slots: MutableList<GameUnit> = mutableListOf()
    private val frozenItems: MutableSet<Int> = mutableSetOf()

    init {
        generateInitialShop()
    }

    fun startTurn() {
        // Remove non-frozen items and refresh shop
        reroll()
    }

    fun reroll() {
        // Ensure frozen items remain in place
        val newSlots = mutableListOf<GameUnit>()
        for (i in slots.indices) {
            if (i in frozenItems) {
                newSlots.add(slots[i])  // Keep frozen items
            } else {
                newSlots.add(generateShopSlot())  // Replace others
            }
        }
        slots.clear()
        slots.addAll(newSlots)
    }

    fun toggleFreeze(pos: Int) {
        if (pos in frozenItems) {
            frozenItems.remove(pos)
            slots[pos].toggleFreeze() // change the atribute 'isFreeze' at shop slot
        } else {
            frozenItems.add(pos)
            slots[pos].toggleFreeze()
        }
    }

    fun getSlot(pos: Int): GameUnit {
        return slots[pos]
    }

    fun buy(pos: Int): GameUnit {
        if (pos !in slots.indices) return Empty()  // Invalid slot
        val item = slots[pos]
        slots[pos] = Empty() // Remove item from shop after purchase
        return item
    }

    private fun generateInitialShop() {
        slots.clear()
        repeat(5) { slots.add(generateShopSlot()) }  // Assuming 5 shop slots
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
}
