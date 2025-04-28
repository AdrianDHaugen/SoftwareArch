package io.github.super_auto_pets.unit

import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Files.FileType
import com.badlogic.gdx.files.FileHandle
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.factories.ItemFactory
import io.github.super_auto_pets.factories.SpriteFactory
import io.github.super_auto_pets.main.main
import io.github.super_auto_pets.models.Empty
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExtraCoverageTests {

    companion object {
        private val spriteJson = """[{"name":"x","attack":1,"health":1,"tier":1,"level":1,"cost":1,"isFrozen":false,"color":"c","path":""}]"""
        private val itemJson   = """[{"name":"i","cost":1,"isFrozen":false,"addHealth":0,"addAttack":0,"path":""}]"""

        @JvmStatic
        @BeforeAll
        fun stubGdxFiles(): Unit {
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
        }
    }

    // ----------------------------------------
    // 1) exercise the two handleTurn loops in main()
    // ----------------------------------------
    @Test fun exerciseMainHandleTurns() {
        // we need two "-1" inputs: one for Player A, one for Player B
        val simulatedInput = "-1\n-1\n"
        System.setIn(ByteArrayInputStream(simulatedInput.toByteArray()))
        // this will run both handleTurn("A") and ("B"), then do one battle.nextAttackStep()
        main()
        // if it completes without hanging, we've covered main and its branches
    }

    // ----------------------------------------
    // 2) ItemFactory.createItem (default + custom)
    // ----------------------------------------
    @Test fun testItemFactoryCreateItemDefaults() {
        val it = ItemFactory.createItem("foo")
        assertEquals("foo", it.name)
        assertEquals(3,    it.cost)        // default
        assertEquals(0,    it.addHealth)
        assertEquals(0,    it.addAttack)
        assertFalse(it.isFrozen)
        assertEquals("",   it.path)
    }

    @Test fun testItemFactoryCreateItemCustom() {
        val it = ItemFactory.createItem(
            name      = "bar",
            cost      = 5,
            addHealth = 2,
            addAttack = 3,
            isFrozen  = true,
            path      = "/p"
        )
        assertEquals("bar", it.name)
        assertEquals(5,     it.cost)
        assertEquals(2,     it.addHealth)
        assertEquals(3,     it.addAttack)
        assertTrue(it.isFrozen)
        assertEquals("/p",  it.path)
    }

    // ----------------------------------------
    // 3) SpriteFactory.createSprite (default + custom)
    // ----------------------------------------
    @Test fun testSpriteFactoryCreateSpriteDefaults() {
        val sp = SpriteFactory.createSprite("s1", attack = 2, health = 3)
        assertEquals("s1", sp.name)
        assertEquals(2,    sp.attack)
        assertEquals(3,    sp.health)
        assertEquals(1,    sp.tier)      // default
        assertEquals(1,    sp.level)
        assertEquals(3,    sp.cost)
        assertFalse(sp.isFrozen)
        assertEquals("base", sp.color)
        assertEquals("",     sp.path)
        assertNull(sp.item)
    }

    @Test fun testSpriteFactoryCreateSpriteCustom() {
        val itm = ItemFactory.createItem("ii", cost = 7)
        val sp = SpriteFactory.createSprite(
            name     = "s2",
            attack   = 4,
            health   = 5,
            tier     = 2,
            level    = 3,
            cost     = 6,
            isFrozen = true,
            color    = "red",
            path     = "/x",
            item     = itm
        )
        assertEquals("s2", sp.name)
        assertEquals(4,    sp.attack)
        assertEquals(5,    sp.health)
        assertEquals(2,    sp.tier)
        assertEquals(3,    sp.level)
        assertEquals(6,    sp.cost)
        assertTrue(sp.isFrozen)
        assertEquals("red", sp.color)
        assertEquals("/x",   sp.path)
        assertSame(itm,      sp.item)
    }

    // ----------------------------------------
    // 4) a quick PlayerController.buy(...) success
    // ----------------------------------------
    @Test fun testPlayerControllerBuySuccess() {
        val p    = Player()
        val ctrl = PlayerController(p)
        // ensure shop[0] is a Sprite we can afford
        // shops are populated in the ShopController ctor via JSON above
        assertTrue(p.shop.slots[0] is Sprite)
        // ensure team[0] is initially Empty
        assertTrue(p.team.teams[0] is Empty)
        // do the buy
        val code = ctrl.buy(itemPos = 0, targetPos = 0)
        assertEquals(1, code)
        // now team[0] should be a Sprite
        assertTrue(p.team.teams[0] is Sprite)
    }
}
