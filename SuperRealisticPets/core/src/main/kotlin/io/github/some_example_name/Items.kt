package io.github.some_example_name

import io.github.some_example_name.Sprite

open class Item(
    val name: String
) {
    open fun onTurnStart(holder: Sprite) {}
    open fun onBattleStart(holder: Sprite) {}
    open fun onAttack(holder: Sprite, target: Sprite) {}
}


