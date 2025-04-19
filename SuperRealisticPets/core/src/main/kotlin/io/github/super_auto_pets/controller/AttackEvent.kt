package io.github.super_auto_pets.controller

import io.github.super_auto_pets.models.Sprite

data class AttackEvent(
    val attacker: Sprite,
    val defender: Sprite,
    val oldAttackerHp: Int,
    val oldDefenderHp: Int,
    val newAttackerHp: Int,
    val newDefenderHp: Int,
    val diedSprites: List<Sprite>
)
