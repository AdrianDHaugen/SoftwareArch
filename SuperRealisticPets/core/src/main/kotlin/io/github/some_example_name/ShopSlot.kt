import io.github.some_example_name.Empty
import io.github.some_example_name.GameUnit

class ShopSlot(var item: GameUnit, var isFrozen: Boolean = false) {

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
        return "ShopSlot(item=${item.name}, cost=${item.cost}, frozen=$isFrozen)"
    }
}
