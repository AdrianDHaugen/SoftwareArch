package io.github.super_auto_pets

import io.github.super_auto_pets.controller.AttackEvent
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AttackEventTest {

    @Test
    fun `properties are assigned correctly`() {
        val attacker = Sprite()
        val defender = Sprite()
        val oldAttackerHp = 5
        val oldDefenderHp = 3
        val newAttackerHp = 4
        val newDefenderHp = 2
        val diedSprites = listOf(defender)

        val event = AttackEvent(
            attacker,
            defender,
            oldAttackerHp,
            oldDefenderHp,
            newAttackerHp,
            newDefenderHp,
            diedSprites
        )

        assertEquals(attacker, event.attacker)
        assertEquals(defender, event.defender)
        assertEquals(oldAttackerHp, event.oldAttackerHp)
        assertEquals(oldDefenderHp, event.oldDefenderHp)
        assertEquals(newAttackerHp, event.newAttackerHp)
        assertEquals(newDefenderHp, event.newDefenderHp)
        assertEquals(diedSprites, event.diedSprites)
    }

    @Test
    fun `equals and hashCode`() {
        val attacker = Sprite()
        val defender = Sprite()
        val event1 = AttackEvent(attacker, defender, 5, 3, 4, 2, emptyList())
        val event2 = AttackEvent(attacker, defender, 5, 3, 4, 2, emptyList())

        assertEquals(event1, event2)
        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `destructuring works`() {
        val attacker = Sprite()
        val defender = Sprite()
        val event = AttackEvent(attacker, defender, 1, 2, 3, 4, emptyList())
        val (a, d, oldA, oldD, newA, newD, died) = event

        assertEquals(attacker, a)
        assertEquals(defender, d)
        assertEquals(1, oldA)
        assertEquals(2, oldD)
        assertEquals(3, newA)
        assertEquals(4, newD)
        assertTrue(died.isEmpty())
    }

    @Test
    fun `copy retains immutability`() {
        val attacker = Sprite()
        val defender = Sprite()
        val event = AttackEvent(attacker, defender, 1, 2, 3, 4, emptyList())
        val modified = event.copy(newAttackerHp = 0)

        assertEquals(0, modified.newAttackerHp)
        // other properties unchanged
        assertEquals(event.attacker, modified.attacker)
        assertEquals(event.defender, modified.defender)
        assertEquals(event.oldAttackerHp, modified.oldAttackerHp)
        assertEquals(event.oldDefenderHp, modified.oldDefenderHp)
        assertEquals(event.newDefenderHp, modified.newDefenderHp)
        assertEquals(event.diedSprites, modified.diedSprites)
    }

    @Test
    fun `toString outputs all properties`() {
        val attacker = Sprite()
        val defender = Sprite()
        val event = AttackEvent(attacker, defender, 10, 8, 9, 7, listOf(attacker))
        val output = event.toString()

        assertTrue(output.contains("AttackEvent"))
        assertTrue(output.contains("oldAttackerHp=10"))
        assertTrue(output.contains("oldDefenderHp=8"))
        assertTrue(output.contains("newAttackerHp=9"))
        assertTrue(output.contains("newDefenderHp=7"))
        assertTrue(output.contains("diedSprites=["))
    }
}
