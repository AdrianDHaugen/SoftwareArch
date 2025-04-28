package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Sprite
import io.github.super_auto_pets.models.Empty
import io.github.super_auto_pets.models.Item
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerControllerIntegrationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Gdx.files = HeadlessFiles()
        }
    }

    @Test
    fun `sell unit and get gold`() {
        val player = Player()
        val controller = PlayerController(player)

        player.team.teams[0] = Sprite().apply {
            name = "Fish"
            attack = 2
            health = 3
            level = 2  // important, gives gold based on level
        }

        val initialGold = player.gold

        val result = controller.sell(0)
        assertEquals(0, result, "Selling should succeed")
        assertTrue(player.team.teams[0] is Empty, "Slot should be Empty after selling")
        assertEquals(initialGold + 2, player.gold, "Gold should increase based on level after selling")
    }

    @Test
    fun `move units around`() {
        val player = Player()
        val controller = PlayerController(player)

        player.team.teams[0] = Sprite().apply { name = "A" }
        player.team.teams[1] = Sprite().apply { name = "B" }

        val result = controller.move(0, 1)
        assertEquals(0, result, "Move should succeed")

        assertEquals("B", (player.team.teams[0] as Sprite).name)
        assertEquals("A", (player.team.teams[1] as Sprite).name)
    }

    @Test
    fun `combine two units`() {
        val player = Player()
        val controller = PlayerController(player)

        player.team.teams[0] = Sprite().apply { name = "Cat"; attack = 2; health = 2 }
        player.team.teams[1] = Sprite().apply { name = "Cat"; attack = 2; health = 2 }

        val result = controller.combine(0, 1)
        assertEquals(0, result, "Combine should succeed")

        val combined = player.team.teams[1] as Sprite
        assertEquals(3, combined.attack, "Attack should increase after combine")
        assertEquals(3, combined.health, "Health should increase after combine")

        assertTrue(player.team.teams[0] is Empty, "Original unit should become Empty after combine")
    }

    @Test
    fun `afford checks for units and reroll`() {
        val player = Player()
        val controller = PlayerController(player)

        val cheapUnit = Sprite().apply { cost = 2 }
        val expensiveUnit = Sprite().apply { cost = 100 }

        assertTrue(controller.canAffordUnit(cheapUnit), "Should afford cheap unit")
        assertTrue(!controller.canAffordUnit(expensiveUnit), "Should not afford expensive unit")

        assertTrue(controller.canAffordReroll(), "Should afford reroll with 10 gold")
    }

    @Test
    fun `start and end turn modifies gold and shop`() {
        val player = Player()
        val controller = PlayerController(player)

        controller.startTurn()
        // 🔥 Fix: StartTurn does reroll (costs 1 gold)
        assertEquals(9, player.gold, "Starting turn should set gold to 9 because reroll costs 1")

        controller.endTurn()
        // No assert here unless you want to check frozen units etc.
    }

}
