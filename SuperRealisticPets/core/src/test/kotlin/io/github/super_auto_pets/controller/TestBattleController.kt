package io.github.super_auto_pets.controller

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.interfaces.HighscoreService
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

class BattleControllerTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // headless Gdx for file operations
            Gdx.files = HeadlessFiles()
            // create dummy JSON files so parsing doesn't fail
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "sprites.json").writeText(
                """[
                    {"name": "cat", "attack": 2, "health": 2, "tier": 1, "item": null, "level": 1, "cost": 3, "isFrozen": false, "color": "base", "path": "cat-1-base-nb.PNG"}
                ]""")
            File(unitsDir, "items.json").writeText(
                """[
                    {"name": "hat", "cost": 3, "isFrozen": false, "addHealth": 1, "addAttack": 1, "path": "cowboyhat.png"}
                ]""")
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            // clean up dummy JSON files and directory
            val unitsDir = File("units")
            unitsDir.listFiles()?.forEach { it.delete() }
            unitsDir.delete()
        }
    }

    // Only testing the simple attack() method
    private class FakeHighscoreService : HighscoreService {
        override fun updateHighscore(playerName: String, winStreak: Int) {
            // no-op
        }
    }

    private fun newSprite(attack: Int, health: Int, name: String = ""): Sprite {
        return Sprite().apply {
            this.attack = attack
            this.health = health
            this.name = name
        }
    }

    @Test
    fun `attack modifies health symmetrically`() {
        val controller = BattleController(Battle(), FakeHighscoreService())
        val s1 = newSprite(7, 10)
        val s2 = newSprite(3, 8)

        controller.attack(s1, s2)

        assertEquals(10 - 3, s1.health)
        assertEquals(8 - 7, s2.health)
    }
}
