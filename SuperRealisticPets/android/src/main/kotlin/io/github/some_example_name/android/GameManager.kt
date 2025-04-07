package io.github.some_example_name.android

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont

class GameManager : Game() {

    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()

        // Set the initial screen
        this.screen = MainMenuScreen(this)  // Set the main menu screen as the initial screen
    }

    override fun render() {
        super.render()  // This renders the current screen
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}
