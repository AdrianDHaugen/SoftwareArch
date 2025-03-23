class Shop {
    private val slots: MutableList<ShopSlot> = mutableListOf()
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
        val newSlots = mutableListOf<ShopSlot>()
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
        } else {
            frozenItems.add(pos)
        }
    }

    fun getSlot(pos: Int): ShopSlot {
        return slots.getOrNull(pos) ?: ShopSlot(Empty())
    }

    fun buy(pos: Int): ShopSlot {
        if (pos !in slots.indices) return ShopSlot(Empty()) // Invalid slot
        val item = slots[pos]
        slots[pos] = ShopSlot(Empty())  // Remove item from shop after purchase
        return item
    }

    private fun generateInitialShop() {
        slots.clear()
        repeat(5) { slots.add(generateShopSlot()) }  // Assuming 5 shop slots
    }

    private fun generateShopSlot(): ShopSlot {
        // Generate a random item (Animal or Equipment)
        val item = if ((0..1).random() == 0) generateRandomAnimal() else generateRandomEquipment()
        return ShopSlot(item)
    }

    private fun generateRandomAnimal(): Animal {
        return Animal("Animal ${(1..10).random()}", 1, 1, 3)  // Example random animal. Here we have to return the animal object. The values after comma are for health, attack and level
    }

    private fun generateRandomEquipment(): Equipment {
        return Equipment("Equipment ${(1..5).random()}", 1)  // Example random equipment
    }
}
