package io.github.some_example_name.models

import io.github.some_example_name.interfaces.GameUnit

open class Empty(): GameUnit {

    override val name: String = "Empty"
    override val cost: Int = 100
    override var isFrozen: Boolean = false

}
