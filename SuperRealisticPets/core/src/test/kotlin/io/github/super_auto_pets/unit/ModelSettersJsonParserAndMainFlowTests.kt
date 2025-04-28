package io.github.super_auto_pets.unit

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.files.FileHandle
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.controller.spritesDB
import io.github.super_auto_pets.controller.itemsDB
import io.github.super_auto_pets.factories.SpriteFactory
import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.main.main
import io.github.super_auto_pets.models.*
import io.github.super_auto_pets.utilities.JsonParser
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoreCoverageTests {

    companion object {
        private val spriteJson = """[
      { "name":"x","attack":1,"health":1,"tier":1,"level":1,"cost":1,"isFrozen":false,"color":"c","path":"" }
    ]"""
        private val itemJson = """[
      { "name":"i","cost":1,"isFrozen":false,"addHealth":0,"addAttack":0,"path":"" }
    ]"""

        @JvmStatic
        @BeforeAll
        fun setupGdxAndDB(): Unit {
            // stub Gdx.files
            Gdx.files = object : Files {
                private fun fh(path: String) = object : FileHandle(path) {
                    override fun readString(): String =
                        if (path.contains("units/sprites.json")) spriteJson else itemJson
                }

                override fun internal(path: String): FileHandle = fh(path)
                override fun classpath(path: String): FileHandle = fh(path)
                override fun external(path: String): FileHandle = fh(path)
                override fun absolute(path: String): FileHandle = fh(path)
                override fun local(path: String): FileHandle = fh(path)
                override fun getFileHandle(path: String, type: FileType): FileHandle = fh(path)
                override fun getExternalStoragePath(): String = ""
                override fun isExternalStorageAvailable(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun getLocalStoragePath(): String = ""
                override fun isLocalStorageAvailable(): Boolean {
                    TODO("Not yet implemented")
                }
            }
            // reset global DBs
            spritesDB = emptyList()
            itemsDB = emptyList()
            // force parse to populate
            JsonParser().parseSprites()
            JsonParser().parseItems()
        }
    }

    @Test fun testPlayerSettersAndGetters() {
        val p = Player()
        p.name = "Bob"
        p.gold = 5
        p.turn = 3
        val t = Team()
        p.team = t
        val shop = Shop()
        p.shop = shop
        val ctrl = ShopController(p)
        p.shopController = ctrl

        assertEquals("Bob", p.name)
        assertEquals(5, p.gold)
        assertEquals(3, p.turn)
        assertSame(t, p.team)
        assertSame(shop, p.shop)
        assertSame(ctrl, p.shopController)
    }

    @Test fun testShopSettersAndGetters() {
        val shop = Shop()
        val newSlots = mutableListOf<GameUnit>(Empty())
        val newFrozen = mutableSetOf(2)
        shop.slots = newSlots
        shop.frozenUnits = newFrozen
        assertSame(newSlots, shop.slots)
        assertSame(newFrozen, shop.frozenUnits)
    }

    @Test fun testTeamSetter() {
        val team = Team()
        val list = mutableListOf<GameUnit>(Sprite())
        team.teams = list
        assertSame(list, team.teams)
    }

    @Test fun testJsonParserGlobals() {
        // parseSprites/parseItems populate the controller globals
        assertEquals(1, spritesDB.size)
        assertEquals(1, itemsDB.size)
    }

    @Test fun testSpriteFactoryCopyNoItem() {
        val s = Sprite().apply {
            name = "n"
            attack = 1
            health = 1
            tier = 1
            level = 1
            cost = 1
            isFrozen = false
            color = "c"
            path = ""
            item = null
        }
        val c = SpriteFactory.createCopy(s)
        assertNull(c.item)
    }

    @Test fun testMainBranches() {
        // drive handleTurn through all branches
        val inputs = listOf(
            "x",       // invalid → continue
            "0","0", // buy animal
            "5","0", // buy item
            "7","0", // sell
            "8","0", // toggle
            "9",      // reroll
            "10","0","0", // move
            "11","0","0", // combine
            "-1",     // end turn A
            "-1"      // end turn B
        ).joinToString("\n") + "\n"
        System.setIn(ByteArrayInputStream(inputs.toByteArray()))
        main()
    }
}
