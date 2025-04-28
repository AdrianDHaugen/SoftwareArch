package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.*
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopControllerIntegrationTest {

    @BeforeAll
    fun setup() {
        Gdx.files = HeadlessFiles() // headless mode for LibGDX
    }

    @Test
    fun `buying a unit decreases gold and places unit in team`() {
        val player = Player()
        val controller = ShopController(player)

        val initialGold = player.gold
        controller.buy(0, 0) // buy from shop slot 0 into team position 0

        assertTrue(player.team.teams[0] != null, "Unit should be placed on the team.")
        assertTrue(player.gold < initialGold, "Gold should have decreased after buying a unit.")
    }

    @Test
    fun `buying an item decreases gold and attaches to unit`() {
        val player = Player()
        val controller = ShopController(player)

        // Buy a unit first so we can attach an item
        controller.buy(0, 0)

        val initialGold = player.gold
        controller.buy(5, 0) // buy from shop slot 5 (item slot) onto team slot 0

        val unit = player.team.teams[0]
        assertTrue(unit is Sprite, "Unit should be a Sprite.")
        assertTrue((unit as Sprite).item != null, "Item should be attached to unit.")

        assertTrue(player.gold < initialGold, "Gold should have decreased after buying an item.")
    }

    @Test
    fun `rerolling shop generates new units`() {
        val player = Player()
        val controller = ShopController(player)

        val originalNames = player.shop.slots.map { it.name }
        controller.reroll()
        val newNames = player.shop.slots.map { it.name }

        assertNotEquals(originalNames, newNames, "Shop slots should change after rerolling.")
    }

    @Test
    fun `freeze and unfreeze a shop slot`() {
        val player = Player()
        val controller = ShopController(player)

        // Freeze slot 0
        controller.toggleFreeze(0)

        // frozenUnits is a list of slot indices, so just check index 0 is in it
        assertTrue(player.shop.frozenUnits.contains(0), "Slot 0 should be frozen after toggle.")

        // Unfreeze slot 0
        controller.toggleFreeze(0)

        // Now check it's unfrozen
        assertFalse(player.shop.frozenUnits.contains(0), "Slot 0 should be unfrozen after second toggle.")
    }

}
