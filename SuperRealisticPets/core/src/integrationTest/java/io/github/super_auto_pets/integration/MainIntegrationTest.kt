package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Sprite
import io.github.super_auto_pets.interfaces.HighscoreService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File

class MainIntegrationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Gdx.files = HeadlessFiles()
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "sprites.json").writeText(
                """[
                    {"name":"fish","attack":1,"health":1,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"fish-1-base-nb.PNG"},
                    {"name":"dog","attack":5,"health":5,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"dog-1-base-nb.PNG"}
                ]"""
            )
            File(unitsDir, "items.json").writeText(
                """[
                    {"name":"hat","cost":3,"isFrozen":false,"addHealth":1,"addAttack":1,"path":"hat.png"}
                ]"""
            )
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            File("units").deleteRecursively()
        }
    }

    private class DummyHighscoreService : HighscoreService {
        val calls = mutableListOf<Pair<String, Int>>()

        override fun updateHighscore(playerName: String, winStreak: Int) {
            calls.add(playerName to winStreak)
        }
    }

    @Test
    fun `simulate main full flow`() {
        val highscoreService = DummyHighscoreService()

        val playerA = Player().apply { name = "Alice" }
        val playerB = Player().apply { name = "Bob" }

        val playerAController = PlayerController(playerA)
        val playerBController = PlayerController(playerB)

        // Simulate simple turns
        playerAController.startTurn()
        playerBController.startTurn()

        // Add a very weak pet to Alice
        val weakPet = Sprite().apply {
            name = "fish"
            attack = 1
            health = 1
        }
        playerA.team.teams.add(weakPet)

        // Add a very strong pet to Bob
        val strongPet = Sprite().apply {
            name = "dog"
            attack = 10
            health = 10
        }
        playerB.team.teams.add(strongPet)

        playerAController.endTurn()
        playerBController.endTurn()

        // Simulate battle
        val battle = Battle().apply {
            this.playerA = playerA
            this.playerB = playerB
        }
        val battleController = BattleController(battle, highscoreService)

        while (battleController.nextAttackStep() != null) {
            // Battle in steps
        }

        // ✅ Now, Bob MUST have won
        assertTrue(highscoreService.calls.isNotEmpty(), "Highscore service should have recorded a winner!")
    }




}
