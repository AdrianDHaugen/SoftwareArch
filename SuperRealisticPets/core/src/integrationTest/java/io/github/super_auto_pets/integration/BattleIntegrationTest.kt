package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.interfaces.HighscoreService
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File

class BattleIntegrationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Gdx.files = HeadlessFiles()
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "/sprites.json").writeText(
                """[
                  {"name":"dog","attack":3,"health":6,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"dog-1-base.png"},
                  {"name":"cat","attack":2,"health":3,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"cat-1-base.png"},
                  {"name":"fish","attack":4,"health":5,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"fish-1-base.png"}
                ]"""
            )
            File(unitsDir, "/items.json").writeText(
                """[
      {"name":"hat","cost":3,"isFrozen":false,"addHealth":1,"addAttack":1,"path":"cowboyhat.png"}
    ]"""
            )

        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            File("units").deleteRecursively()
        }
    }

    private fun newSprite(name: String, attack: Int, health: Int): Sprite = Sprite().apply {
        this.name = name
        this.attack = attack
        this.health = health
    }

    private class SpyHighscoreService : HighscoreService {
        val calls = mutableListOf<Pair<String, Int>>()
        override fun updateHighscore(playerName: String, winStreak: Int) {
            calls.add(playerName to winStreak)
        }
    }

    @Test
    fun `full battle flow player A wins with multiple pets`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.name = "Alice"
            playerB.name = "Bob"
            playerA.team.teams.addAll(listOf(
                newSprite("dog", 3, 6),
                newSprite("cat", 2, 3)
            ))
            playerB.team.teams.addAll(listOf(
                newSprite("fish", 4, 5)
            ))
        }
        val controller = BattleController(battle, spyService)

        while (controller.nextAttackStep() != null) {
            // keep attacking until no more steps
        }

        assertEquals(1, battle.playerA.winStreak)
        assertEquals(0, battle.playerB.winStreak)
        assertEquals(listOf("Alice" to 1), spyService.calls)
    }

    @Test
    fun `full battle flow ends in draw`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.name = "Alice"
            playerB.name = "Bob"
            playerA.team.teams.add(newSprite("dog", 5, 5))
            playerB.team.teams.add(newSprite("cat", 5, 5))
        }
        val controller = BattleController(battle, spyService)

        while (controller.nextAttackStep() != null) {
            // continue battle
        }

        assertEquals(0, battle.playerA.winStreak)
        assertEquals(0, battle.playerB.winStreak)
        assertTrue(spyService.calls.isEmpty())
    }
    @Test
    fun `full battle flow player B wins with strong pet`() {
        val spyService = SpyHighscoreService()
        val battle = Battle()

        // Set player names (VERY IMPORTANT)
        battle.playerA.name = "Alice"
        battle.playerB.name = "Bob"

        // Player A - weak team
        battle.playerA.team.teams.addAll(listOf(
            Sprite().apply { name = "dog"; attack = 2; health = 3 },
            Sprite().apply { name = "cat"; attack = 1; health = 2 }
        ))

        // Player B - strong pet
        battle.playerB.team.teams.add(
            Sprite().apply { name = "fish"; attack = 8; health = 10 }
        )

        val controller = BattleController(battle, spyService)

        // Perform full battle
        while (controller.nextAttackStep() != null) {
            // Step until someone wins
        }

        // Assert: Player B wins
        assertEquals(0, battle.playerA.winStreak, "Player A should have 0 win streak")
        assertEquals(1, battle.playerB.winStreak, "Player B should have 1 win streak")
        assertEquals(listOf("Bob" to 1), spyService.calls, "Highscore service should record Bob's win")
    }


}
