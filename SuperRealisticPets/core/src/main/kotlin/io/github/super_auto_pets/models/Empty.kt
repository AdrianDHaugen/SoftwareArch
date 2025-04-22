package io.github.super_auto_pets.models

import io.github.super_auto_pets.interfaces.GameUnit

open class Empty(): GameUnit {

    override val name: String = "Empty"
    override val cost: Int = 100
    override var isFrozen: Boolean = false
    override var path: String = ""

}
