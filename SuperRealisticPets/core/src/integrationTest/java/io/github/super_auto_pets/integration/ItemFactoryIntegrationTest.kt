package io.github.super_auto_pets.integration.io.github.super_auto_pets.integration

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.headless.HeadlessFiles
import io.github.super_auto_pets.factories.ItemFactory
import io.github.super_auto_pets.models.Sprite
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class ItemFactoryIntegrationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            Gdx.files = HeadlessFiles()

            // Minimal setup if any assets are needed (optional here)
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
    fun `created item can be attached to sprite and boost stats`() {
        // Arrange
        val item = ItemFactory.createItem(
            name = "Mega Hat",
            addAttack = 2,
            addHealth = 3
        )

        val sprite = Sprite().apply {
            name = "Doggo"
            attack = 5
            health = 5
        }

        // Act
        sprite.item = item
        sprite.attack += item.addAttack
        sprite.health += item.addHealth

        // Assert
        assertEquals(7, sprite.attack, "Attack should be boosted by item")
        assertEquals(8, sprite.health, "Health should be boosted by item")
        assertEquals("Mega Hat", sprite.item?.name)
    }
}
