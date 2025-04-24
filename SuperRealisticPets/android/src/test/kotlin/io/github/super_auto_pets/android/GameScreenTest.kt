package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.backends.headless.HeadlessFiles
import com.badlogic.gdx.backends.headless.HeadlessNativesLoader
import com.badlogic.gdx.graphics.GL20
import io.github.super_auto_pets.controller.GameMode
import io.github.super_auto_pets.models.Sprite
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameScreenTest {

    private lateinit var mockGame: Main
    private lateinit var mockGL: GL20

    @Before
    fun setUp() {
        // Load natives for headless environment
        HeadlessNativesLoader.load()

        // Mock essential LibGDX components
        mockGL = mock(GL20::class.java)
        Gdx.gl = mockGL

        // Set up files system
        Gdx.files = HeadlessFiles()

        // Mock the game
        mockGame = mock(Main::class.java)
        `when`(mockGame.highscoreService).thenReturn(mock())
    }

    @Test
    fun `test team creation`() {
        // Create some test sprites
        val cat = Sprite().apply { name = "cat"; health = 3; attack = 2 }
        val dog = Sprite().apply { name = "dog"; health = 4; attack = 3 }

        // Test that we can create sprites correctly
        assertEquals("cat", cat.name)
        assertEquals(3, cat.health)
        assertEquals(2, cat.attack)

        assertEquals("dog", dog.name)
        assertEquals(4, dog.health)
        assertEquals(3, dog.attack)
    }

    @Test
    fun `test random team generation`() {
        // Test the generateRandomTeam function directly
        val randomTeam = generateRandomTeam()

        // Check basic properties
        assertEquals(4, randomTeam.size)

        // Verify each sprite has valid values
        randomTeam.forEach { sprite ->
            assertNotNull(sprite.name)
            assert(sprite.health > 0)
            assert(sprite.attack > 0)
        }
    }
}
