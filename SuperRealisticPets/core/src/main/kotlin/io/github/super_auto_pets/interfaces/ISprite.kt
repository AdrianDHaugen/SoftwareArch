package io.github.super_auto_pets.interfaces

import io.github.super_auto_pets.models.Item


interface ISprite : GameUnit {
        // Define all properties that Sprite must provide
        override var name: String
        var attack: Int
        var health: Int
        var tier: Int
        var item: Item?
        var level: Int
        override var cost: Int
        override var isFrozen: Boolean

}
