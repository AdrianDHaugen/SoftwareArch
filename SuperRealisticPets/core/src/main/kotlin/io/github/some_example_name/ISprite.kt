package io.github.some_example_name


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

        // Define all methods that Sprite must implement
        fun onTurnStart()
        fun onBattleStart()
        fun attack(target: Sprite)
        fun mergeStats(sprite1: Sprite, sprite2: Sprite)
        fun isAlive(): Boolean
}
