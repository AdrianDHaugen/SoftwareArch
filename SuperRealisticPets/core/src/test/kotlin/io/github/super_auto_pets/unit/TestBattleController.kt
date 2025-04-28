package io.github.super_auto_pets.unit

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

class BattleControllerTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // headless libGDX
            Gdx.files = HeadlessFiles()
            // create minimal JSON stores
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "/sprites.json").writeText(
                """[
                  {"name":"cat","attack":2,"health":2,"tier":1,"item":null,"level":1,"cost":3,"isFrozen":false,"color":"base","path":"cat-1-base-nb.PNG"}
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

    private fun newSprite(attack: Int, health: Int, name: String = ""): Sprite = Sprite().apply {
        this.attack = attack
        this.health = health
        this.name = name
    }

    private class SpyHighscoreService : HighscoreService {
        val calls = mutableListOf<Pair<String, Int>>()
        override fun updateHighscore(playerName: String, winStreak: Int) {
            calls.add(playerName to winStreak)
        }
    }

    @Test
    fun `attack modifies health symmetrically`() {
        val controller = BattleController(Battle(), SpyHighscoreService())
        val s1 = newSprite(7, 10, "A")
        val s2 = newSprite(3, 8, "B")

        controller.attack(s1, s2)

        assertEquals(8 - 7, s2.health)
        assertEquals(10 - 3, s1.health)
    }

    @Test
    fun `nextAttackStep when both alive returns event and removes dead`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.team.teams.add(newSprite(3, 5, "X"))
            playerB.team.teams.add(newSprite(1, 2, "Y"))
        }
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNotNull(event)
        event!!

        // X attacks Y: Y dies, X survives
        assertEquals("X", event.attacker.name)
        assertEquals("Y", event.defender.name)
        assertEquals(5, event.oldAttackerHp)
        assertEquals(2, event.oldDefenderHp)
        assertEquals(4, event.newAttackerHp)
        assertEquals(-1, event.newDefenderHp)
        assertEquals(listOf(event.defender), event.diedSprites)

        // ensure removal: only X remains among Sprites, Y removed
        val remainingA = battle.playerA.team.teams.filterIsInstance<Sprite>()
        val remainingB = battle.playerB.team.teams.filterIsInstance<Sprite>()
        assertEquals(1, remainingA.size)
        assertEquals(event.attacker, remainingA.first())
        assertTrue(remainingB.isEmpty())
        assertTrue(spyService.calls.isEmpty())
    }

    @Test
    fun `nextAttackStep when attacker dies only removes attacker`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.team.teams.add(newSprite(1, 2, "A"))
            playerB.team.teams.add(newSprite(3, 5, "B"))
        }
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNotNull(event)
        event!!

        // A dies, B survives
        assertEquals(listOf(event.attacker), event.diedSprites)
        assertTrue(battle.playerA.team.teams.filterIsInstance<Sprite>().isEmpty())
        val remainingB = battle.playerB.team.teams.filterIsInstance<Sprite>()
        assertEquals(1, remainingB.size)
        assertEquals(event.defender, remainingB.first())
    }

    @Test
    fun `nextAttackStep when both die returns event and removes both`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.team.teams.add(newSprite(5, 3, "A"))
            playerB.team.teams.add(newSprite(4, 2, "B"))
        }
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNotNull(event)
        event!!

        // both die
        assertTrue(event.diedSprites.containsAll(listOf(event.attacker, event.defender)))
        assertTrue(battle.playerA.team.teams.filterIsInstance<Sprite>().isEmpty())
        assertTrue(battle.playerB.team.teams.filterIsInstance<Sprite>().isEmpty())
    }

    @Test
    fun `nextAttackStep when player A wins updates highscore and resets B`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.name = "Alice"
            playerB.name = "Bob"
            playerA.team.teams.add(newSprite(99, 1))
        }
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNull(event)
        assertEquals(1, battle.playerA.winStreak)
        assertEquals(0, battle.playerB.winStreak)
        assertEquals(listOf("Alice" to 1), spyService.calls)
    }

    @Test
    fun `nextAttackStep when player B wins updates highscore and resets A`() {
        val spyService = SpyHighscoreService()
        val battle = Battle().apply {
            playerA.name = "Alice"
            playerB.name = "Bob"
            playerB.team.teams.add(newSprite(10, 1))
        }
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNull(event)
        assertEquals(0, battle.playerA.winStreak)
        assertEquals(1, battle.playerB.winStreak)
        assertEquals(listOf("Bob" to 1), spyService.calls)
    }

    @Test
    fun `nextAttackStep when draw resets both winStreaks without calling highscore`() {
        val spyService = SpyHighscoreService()
        val battle = Battle()
        val controller = BattleController(battle, spyService)

        val event = controller.nextAttackStep()
        assertNull(event)
        assertEquals(0, battle.playerA.winStreak)
        assertEquals(0, battle.playerB.winStreak)
        assertTrue(spyService.calls.isEmpty())
    }
}
