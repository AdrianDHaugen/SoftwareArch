package io.github.some_example_name.item

import io.github.some_example_name.sprite.Sprite

open class Item(
    val name: String,
    val cost: Int,
) {
    open fun onTurnStart(holder: Sprite) {}
    open fun onBattleStart(holder: Sprite) {}
    open fun onAttack(holder: Sprite, target: Sprite) {}
}

}
