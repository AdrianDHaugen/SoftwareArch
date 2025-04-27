package io.github.super_auto_pets.controller

import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.interfaces.HighscoreService
import io.github.super_auto_pets.models.Battle
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import java.io.File

class BattleControllerTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupGdx() {
            // initialize headless file resolver
            Gdx.files = HeadlessFiles()
            // create a dummy sprites.json so JsonParser can find it
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "sprites.json").writeText("[]")
            // create a dummy items.json so JsonParser can find it
            File(unitsDir, "items.json").writeText("[]")
        }
    }

    private class FakeHighscoreService : HighscoreService {
        var callCount = 0
        var lastPlayer: String? = null
        var lastStreak: Int? = null
        override fun updateHighscore(playerName: String, winStreak: Int) {
            callCount++
            lastPlayer = playerName
            lastStreak = winStreak
        }
    }

    private fun newSprite(attack: Int, health: Int, name: String = ""): Sprite {
        val s = Sprite()
        s.attack = attack
        s.health = health
        s.name = name
        return s
    }

    @Test
    fun `nextAttackStep when both alive returns event and removes dead`() {
        val fakeHS = FakeHighscoreService()
        val battle = Battle()
        battle.playerA.name = "A"
        battle.playerB.name = "B"

        val sA1 = newSprite(5, 3, "A1")
        val sB1 = newSprite(2, 4, "B1")
        battle.playerA.team.teams = mutableListOf(sA1)
        battle.playerB.team.teams = mutableListOf(sB1)

        val controller = BattleController(battle, fakeHS)
        val event = controller.nextAttackStep()!!

        // Old HP captured correctly
        assertEquals(3, event.oldAttackerHp)
        assertEquals(4, event.oldDefenderHp)
        // New HP after exchange
        assertEquals(1, event.newAttackerHp)
        assertEquals(-1, event.newDefenderHp)
        // Defender died
        assertEquals(listOf(sB1), event.diedSprites)
        // Dead sprite removed from B's team; A's remains
        assertEquals(listOf(sA1), battle.playerA.team.teams)
        assertTrue(battle.playerB.team.teams.isEmpty())
        // No highscore update on mid-battle
        assertEquals(0, fakeHS.callCount)
    }

    @Test
    fun `nextAttackStep when playerA wins updates highscore`() {
        val fakeHS = FakeHighscoreService()
        val battle = Battle()
        battle.playerA.name = "Alice"
        battle.playerB.name = "Bob"
        // A has one alive, B has none
        battle.playerA.team.teams = mutableListOf(newSprite(0, 1))
        battle.playerB.team.teams = mutableListOf()

        val controller = BattleController(battle, fakeHS)
        val result = controller.nextAttackStep()

        assertNull(result)
        assertEquals(1, battle.playerA.winStreak)
        assertEquals(0, battle.playerB.winStreak)
        assertEquals(1, fakeHS.callCount)
        assertEquals("Alice", fakeHS.lastPlayer)
        assertEquals(1, fakeHS.lastStreak)
    }

    @Test
    fun `nextAttackStep when playerB wins updates highscore`() {
        val fakeHS = FakeHighscoreService()
        val battle = Battle()
        battle.playerA.name = "Alice"
        battle.playerB.name = "Bob"
        // B has one alive, A has none
        battle.playerA.team.teams = mutableListOf()
        battle.playerB.team.teams = mutableListOf(newSprite(0, 1))

        val controller = BattleController(battle, fakeHS)
        val result = controller.nextAttackStep()

        assertNull(result)
        assertEquals(0, battle.playerA.winStreak)
        assertEquals(1, battle.playerB.winStreak)
        assertEquals(1, fakeHS.callCount)
        assertEquals("Bob", fakeHS.lastPlayer)
        assertEquals(1, fakeHS.lastStreak)
    }

    @Test
    fun `attack modifies health symmetrically`() {
        val fakeHS = FakeHighscoreService()
        val controller = BattleController(Battle(), fakeHS)
        val s1 = newSprite(7, 10, "One")
        val s2 = newSprite(3, 8, "Two")

        controller.attack(s1, s2)

        assertEquals(10 - 3, s1.health)
        assertEquals(8 - 7, s2.health)
    }
}
