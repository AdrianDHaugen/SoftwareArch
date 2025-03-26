package io.github.some_example_name

import io.github.some_example_name.sprite.Sprite

open class Item(
    override val name: String,
    override val cost: Int,
): GameUnit {
    open fun onTurnStart(holder: Sprite) {}
    open fun onBattleStart(holder: Sprite) {}
    open fun onAttack(holder: Sprite, target: Sprite) {}
}

}
