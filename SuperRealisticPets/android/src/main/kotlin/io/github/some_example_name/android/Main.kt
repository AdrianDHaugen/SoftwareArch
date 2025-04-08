package io.github.some_example_name.android

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont

class Main : Game() {

    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {

        // Set the initial screen
        setScreen(MainMenuScreen(this))  // Set the main menu screen as the initial screen
    }

    override fun render() {
        Gdx.gl.glClearColor(0.192f, 0.341f, 0.659f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.render()  // This renders the current screen
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}
