package io.github.some_example_name

class ShopSlot(var item: GameUnit, private var isFrozen: Boolean = false) {

    fun toggleFreeze() {
        isFrozen = !isFrozen
    }

    fun buy(): GameUnit {
        if (item is Empty) return Empty() // Prevent buying an empty slot
        val purchasedItem = item
        item = Empty() // Clear the slot after purchase
        return purchasedItem
    }

    override fun toString(): String {
        return "io.github.some_example_name.ShopSlot(item=${item.name}, cost=${item.cost}, frozen=$isFrozen)"
    }
}
