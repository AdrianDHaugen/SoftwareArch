package io.github.super_auto_pets

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.files.FileHandle
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.factories.ItemFactory
import io.github.super_auto_pets.factories.SpriteFactory
import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.main.Main
import io.github.super_auto_pets.models.*
import io.github.super_auto_pets.utilities.JsonParser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SimpleCoverageTests {

    companion object {
        private val spriteJson = """
      [{
        "name":"stub",
        "attack":1,
        "health":1,
        "tier":0,
        "level":0,
        "cost":1,
        "isFrozen":false,
        "color":"c",
        "path":"p"
      }]
    """.trimIndent()
        private val itemJson = """
      [{
        "name":"stubItem",
        "cost":1,
        "isFrozen":false,
        "addHealth":0,
        "addAttack":0,
        "path":"ip"
      }]
    """.trimIndent()

        @JvmStatic
        @BeforeAll
        fun setupGdx(): Unit {
            Gdx.files = object : Files {
                private fun stubHandle(path: String) = object : FileHandle(path) {
                    override fun readString(): String =
                        if (path.contains("sprites.json")) spriteJson else itemJson
                }

                override fun internal(path: String): FileHandle = stubHandle(path)
                override fun classpath(path: String): FileHandle = stubHandle(path)
                override fun external(path: String): FileHandle = stubHandle(path)
                override fun absolute(path: String): FileHandle = stubHandle(path)
                override fun local(path: String): FileHandle = stubHandle(path)
                override fun getFileHandle(path: String, type: FileType): FileHandle =
                    stubHandle(path)

                override fun getExternalStoragePath(): String = ""
                override fun isExternalStorageAvailable(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun getLocalStoragePath(): String = ""
                override fun isLocalStorageAvailable(): Boolean {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    // ------------------------------------
    // Models / Factories / Interfaces
    // ------------------------------------

    @Test fun testEmptyModel() {
        val e = Empty()
        assertEquals("Empty", e.name)
        assertEquals(100, e.cost)
        assertFalse(e.isFrozen)
        assertEquals("", e.path)
    }

    @Test fun testItemModelAndFactory() {
        val item = Item().apply {
            name      = "foo"
            cost      = 7
            addAttack = 2
            addHealth = 3
            isFrozen  = true
            path      = "/p"
        }
        val copy = ItemFactory.createCopy(item)
        assertNotSame(item, copy)
        assertEquals(item.name,      copy.name)
        assertEquals(item.cost,      copy.cost)
        assertEquals(item.addAttack, copy.addAttack)
        assertEquals(item.addHealth, copy.addHealth)
        assertEquals(item.isFrozen,  copy.isFrozen)
        assertEquals(item.path,      copy.path)
        assertTrue(copy.toString().startsWith("Item(name='foo'"))
    }

    @Test fun testSpriteModelAndFactory() {
        val baseItem = Item().apply {
            name      = "i"
            cost      = 5
            addAttack = 1
            addHealth = 1
            isFrozen  = false
            path      = "/i"
        }
        val s = Sprite().apply {
            name      = "bar"
            attack    = 5
            health    = 6
            tier      = 1
            level     = 2
            cost      = 3
            isFrozen  = true
            color     = "red"
            path      = "/s"
            item      = baseItem
        }
        val copy = SpriteFactory.createCopy(s)
        assertNotSame(s, copy)
        assertEquals(s.name,        copy.name)
        assertEquals(s.attack,      copy.attack)
        assertEquals(s.health,      copy.health)
        assertEquals(s.tier,        copy.tier)
        assertEquals(s.level,       copy.level)
        assertEquals(s.cost,        copy.cost)
        assertEquals(s.isFrozen,    copy.isFrozen)
        assertEquals(s.color,       copy.color)
        assertEquals(s.path,        copy.path)
        assertNotSame(s.item,       copy.item)
        assertEquals(baseItem.path, copy.item!!.path)
        assertTrue(copy.toString().contains("Sprite(name='bar'"))
    }

    @Test fun testGameUnitToggleFreeze() {
        val sp = Sprite()
        assertFalse(sp.isFrozen)
        (sp as GameUnit).toggleFreeze()
        assertTrue(sp.isFrozen)
    }

    // ------------------------------------
    // Domain Models (Battle, Team, Player, Shop)
    // ------------------------------------

    @Test fun testBattleAndTeamModels() {
        val b  = Battle()
        val p1 = Player().apply { name = "A" }
        val p2 = Player().apply { name = "B" }
        b.playerA = p1
        b.playerB = p2
        assertSame(p1, b.playerA)
        assertSame(p2, b.playerB)

        val t = Team()
        t.teams.add(Sprite())
        assertEquals(1, t.teams.size)
    }

    @Test fun testShopModel() {
        val shop = Shop()
        assertTrue(shop.slots.isEmpty())
        assertTrue(shop.frozenUnits.isEmpty())
    }

    @Test fun testPlayerModelDefaults() {
        val p = Player()
        // ShopController init & parseGameUnits() ran successfully
        assertEquals(20, p.gold)
        assertEquals(1,  p.turn)
        assertNotNull(p.team)
        assertNotNull(p.shop)
        assertNotNull(p.shopController)
    }

    // ------------------------------------
    // PlayerController
    // ------------------------------------

    @Test fun testPlayerControllerStartTurn() {
        val p    = Player().apply { gold = 3; turn = 5 }
        val ctrl = PlayerController(p)
        ctrl.startTurn()
        assertEquals(6, p.turn)
        // reroll after setting to 10 ⇒ 10−1 = 9
        assertEquals(9, p.gold)
    }

    @Test fun testPlayerControllerBuySellMoveCombineAndHelpers() {
        val p    = Player()
        val ctrl = PlayerController(p)

        // sell on initial (Empty) slot → -1
        assertEquals(-1, ctrl.sell(0))

        // place a real sprite at slot 0, then sell → 0
        val one = Sprite().apply { level = 2; cost = 1; attack = 1; health = 1 }
        p.team.teams[0] = one
        assertEquals(0, ctrl.sell(0))
        assertEquals(20 + one.level, p.gold)

        // move same index → -1
        assertEquals(-1, ctrl.move(0, 0))

        // combine on any slot with an Empty → -1
        assertEquals(-1, ctrl.combine(0, 1))

        // helpers
        assertEquals(p.gold,   ctrl.getPlayerGold())
        assertEquals(one.cost, ctrl.getUnitCost(one))
        ctrl.endTurn()
    }

    // ------------------------------------
    // ShopController
    // ------------------------------------

    @Test fun testShopControllerToggleFreezeRerollAndBuyFail() {
        val p        = Player()
        val shopCtrl = ShopController(p)
        // slots 0–3 are sprites, 4–5 items

        // freeze slot 0
        assertEquals(0, shopCtrl.toggleFreeze(0))
        assertTrue(p.shop.frozenUnits.contains(0))

        // reroll costs 1
        val before = p.gold
        assertEquals(0, shopCtrl.reroll())
        assertEquals(before - 1, p.gold)

        // buy an item (slot 4) onto empty team[0] → -1
        assertEquals(-1, shopCtrl.buy(4, 0))
    }

    // ------------------------------------
    // Utilities
    // ------------------------------------

    @Test fun testJsonParserParsesOneElement() {
        val parser = JsonParser()
        assertEquals(1, parser.parseSprites().size)
        assertEquals(1, parser.parseItems().size)
    }

    // ------------------------------------
    // Main
    // ------------------------------------

    @Test fun testMainInstantiation() {
        Main()
    }
}
