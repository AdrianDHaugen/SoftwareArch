package io.github.super_auto_pets

import io.github.super_auto_pets.models.Empty
import org.junit.Assert.*
import org.junit.Test

class EmptyTest {

    @Test
    fun `default properties are correct`() {
        val empty = Empty()
        assertEquals("Empty", empty.name)
        assertEquals(100, empty.cost)
        assertEquals("", empty.path)
        assertFalse(empty.isFrozen)
    }

    @Test
    fun `toggleFreeze flips the flag`() {
        val empty = Empty()
        // starts unfrozen
        assertFalse(empty.isFrozen)

        empty.toggleFreeze()
        assertTrue(empty.isFrozen)

        empty.toggleFreeze()
        assertFalse(empty.isFrozen)
    }
}
