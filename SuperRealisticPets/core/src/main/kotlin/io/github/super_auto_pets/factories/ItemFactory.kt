package io.github.super_auto_pets.factories

import io.github.super_auto_pets.models.Item

/**
 * Factory for creating and copying Item objects.
 * This ensures that each item instance is independent.
 */
class ItemFactory {
    companion object {
        /**
         * Creates a copy of an existing item with all its properties
         *
         * @param original The item to copy
         * @return A new item with the same properties as the original
         */
        fun createCopy(original: Item): Item {
            val copy = Item()
            copy.name = original.name
            copy.cost = original.cost
            copy.isFrozen = original.isFrozen
            copy.addHealth = original.addHealth
            copy.addAttack = original.addAttack
            copy.path = original.path
            return copy
        }

        /**
         * Creates a new item with the specified properties
         *
         * @param name The name of the item
         * @param cost The cost value
         * @param addHealth The health bonus value
         * @param addAttack The attack bonus value
         * @param isFrozen Whether the item is frozen
         * @param path The path to the item image
         * @return A new item with the specified properties
         */
        fun createItem(
            name: String,
            cost: Int = 3,
            addHealth: Int = 0,
            addAttack: Int = 0,
            isFrozen: Boolean = false,
            path: String = ""
        ): Item {
            val item = Item()
            item.name = name
            item.cost = cost
            item.addHealth = addHealth
            item.addAttack = addAttack
            item.isFrozen = isFrozen
            item.path = path

            return item
        }
    }
}
