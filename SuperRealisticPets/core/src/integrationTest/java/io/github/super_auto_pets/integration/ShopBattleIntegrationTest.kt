package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files
import com.badlogic.gdx.files.FileHandle
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.interfaces.HighscoreService
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Empty
import io.github.super_auto_pets.models.Item
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class ShopBattleIntegrationTest {

    private lateinit var playerController: PlayerController
    private lateinit var shopController: ShopController
    private lateinit var battleController: BattleController
    private lateinit var highscoreService: HighscoreService


    class MockFiles : Files {
        override fun internal(path: String): FileHandle {
            println("Mocking Gdx.files.internal access for: $path")

            // Try to load the file from test resources
            val resource = javaClass.classLoader.getResource(path)
                ?: throw RuntimeException("Test resource file not found: $path")

            return FileHandle(File(resource.toURI()))
        }

        override fun getFileHandle(path: String, type: Files.FileType): FileHandle {
            return internal(path)
        }

        override fun external(path: String?): FileHandle {
            TODO("Not relevant")
        }

        override fun absolute(path: String?): FileHandle {
            TODO("Not relevant")
        }

        override fun local(path: String?): FileHandle {
            TODO("Not relevant")
        }

        override fun getExternalStoragePath(): String {
            TODO("Not relevant")
        }

        override fun isExternalStorageAvailable(): Boolean {
            TODO("Not relevant")
        }

        override fun getLocalStoragePath(): String {
            TODO("Not relevant")
        }

        override fun isLocalStorageAvailable(): Boolean {
            TODO("Not relevant")
        }

        override fun classpath(path: String?): FileHandle {
            TODO("Not relevant")
        }
    }


    @BeforeEach
    fun setup() {

        Gdx.files = MockFiles()

        highscoreService = object : HighscoreService {
            override fun updateHighscore(playerName: String, winStreak: Int) {
                // No-op for tests
            }
        }

        val player = Player() // ✅ No parameters
        playerController = PlayerController(player) // ✅ Create playerController
        shopController = ShopController(player) // ✅ Pass Player (not PlayerController!)

        val battle = Battle() // ✅ No parameters
        battleController = BattleController(battle, highscoreService) // ✅ Pass Battle and HighscoreService
    }





    @Test
    fun `full edit to battle flow`() {
        // 1. Roll shop to refresh
        shopController.reroll()

        // 2. Buy 3 animals
        var shopSlotIndex = 0
        var teamSlotIndex = 0
        val shopSlots = shopController.getShopSlots()
        while (teamSlotIndex < 3 && shopSlotIndex < shopSlots.size) {
            val slot = shopSlots[shopSlotIndex]
            if (slot is Sprite) {
                shopController.buy(shopSlotIndex, teamSlotIndex)
                teamSlotIndex++
            }
            shopSlotIndex++
        }

        // 3. Check team has 3 animals
        val playerTeam = playerController.getTeam()
        assertEquals(3, playerTeam.teams.count { it !is Empty }
            , "Team should have 3 animals after buying")

        // 4. Buy an item
        shopSlotIndex = 0
        while (shopSlotIndex < shopSlots.size) {
            val slot = shopSlots[shopSlotIndex]
            if (slot is Item) {
                shopController.buy(shopSlotIndex, 0)
                break
            }
            shopSlotIndex++
        }

        // 5. Start a battle
        // Battle loop
        var event = battleController.nextAttackStep()
        while (event != null) {
            println("Attack step: ${event.attacker.name} attacked ${event.defender.name}")
            event = battleController.nextAttackStep()
        }

        // If you reach here, battle is finished
        println("Battle finished.")
    }
}
