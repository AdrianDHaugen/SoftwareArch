package io.github.super_auto_pets.factories

import io.github.super_auto_pets.models.Sprite
import io.github.super_auto_pets.models.Item

/**
 * Factory for creating and copying Sprite objects.
 * This ensures that each sprite instance is independent.
 */
class SpriteFactory {
    companion object {
        /**
         * Creates a copy of an existing sprite with all its properties
         *
         * @param original The sprite to copy
         * @return A new sprite with the same properties as the original
         */
        fun createCopy(original: Sprite): Sprite {
            val copy = Sprite()
            copy.name = original.name
            copy.attack = original.attack
            copy.health = original.health
            copy.tier = original.tier
            copy.level = original.level
            copy.cost = original.cost
            copy.isFrozen = original.isFrozen
            copy.color = original.color
            copy.path = original.path

            // Copy item if present
            original.item?.let { originalItem ->
                val itemCopy = ItemFactory.createCopy(originalItem)
                copy.item = itemCopy
            }

            return copy
        }

        /**
         * Creates a new sprite with the specified properties
         *
         * @param name The name of the sprite
         * @param attack The attack value
         * @param health The health value
         * @param tier The tier value
         * @param level The level value
         * @param cost The cost value
         * @param isFrozen Whether the sprite is frozen
         * @param color The color of the sprite
         * @param path The path to the sprite image
         * @param item The item attached to the sprite (optional)
         * @return A new sprite with the specified properties
         */
        fun createSprite(
            name: String,
            attack: Int,
            health: Int,
            tier: Int = 1,
            level: Int = 1,
            cost: Int = 3,
            isFrozen: Boolean = false,
            color: String = "base",
            path: String = "",
            item: Item? = null
        ): Sprite {
            val sprite = Sprite()
            sprite.name = name
            sprite.attack = attack
            sprite.health = health
            sprite.tier = tier
            sprite.level = level
            sprite.cost = cost
            sprite.isFrozen = isFrozen
            sprite.color = color
            sprite.path = path
            sprite.item = item

            return sprite
        }
    }
}
