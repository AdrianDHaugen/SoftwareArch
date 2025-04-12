package io.github.some_example_name.interfaces

import io.github.some_example_name.models.Item


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
