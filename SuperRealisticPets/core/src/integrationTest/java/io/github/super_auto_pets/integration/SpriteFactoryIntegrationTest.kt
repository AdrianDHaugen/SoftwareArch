package io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.factories.SpriteFactory
import io.github.super_auto_pets.models.Item
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class SpriteFactoryIntegrationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Gdx.files = HeadlessFiles()
            // Minimal dummy setup if needed
            val unitsDir = File("units").apply { if (!exists()) mkdirs() }
            File(unitsDir, "sprites.json").writeText("[]")
            File(unitsDir, "items.json").writeText("[]")
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            File("units").deleteRecursively()
        }
    }

    @Test
    fun `createSprite should correctly instantiate a new Sprite`() {
        val sprite = SpriteFactory.createSprite(
            name = "Tiger",
            attack = 7,
            health = 5,
            tier = 3,
            level = 2,
            cost = 4,
            color = "golden",
            path = "tiger-3-golden.png"
        )

        assertEquals("Tiger", sprite.name)
        assertEquals(7, sprite.attack)
        assertEquals(5, sprite.health)
        assertEquals(3, sprite.tier)
        assertEquals(2, sprite.level)
        assertEquals(4, sprite.cost)
        assertEquals("golden", sprite.color)
        assertEquals("tiger-3-golden.png", sprite.path)
        assertNull(sprite.item)
    }

    @Test
    fun `createCopy should produce an independent Sprite with same properties`() {
        val original = Sprite().apply {
            name = "Lion"
            attack = 10
            health = 8
            tier = 5
            level = 1
            cost = 6
            color = "base"
            path = "lion-5-base.png"
            item = Item().apply {
                name = "Magic Hat"
                addAttack = 2
                addHealth = 3
                path = "magic-hat.png"
            }
        }

        val copy = SpriteFactory.createCopy(original)

        // Properties match
        assertEquals(original.name, copy.name)
        assertEquals(original.attack, copy.attack)
        assertEquals(original.health, copy.health)
        assertEquals(original.tier, copy.tier)
        assertEquals(original.level, copy.level)
        assertEquals(original.cost, copy.cost)
        assertEquals(original.color, copy.color)
        assertEquals(original.path, copy.path)

        // Item is deep copied (different object)
        assertNotNull(copy.item)
        assertNotSame(original.item, copy.item)
        assertEquals(original.item?.name, copy.item?.name)
        assertEquals(original.item?.addAttack, copy.item?.addAttack)
        assertEquals(original.item?.addHealth, copy.item?.addHealth)
    }
}
