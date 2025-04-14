package io.github.some_example_name.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.Screen

class GameScreen(private val game: Main) : Screen {
    private lateinit var stage: Stage
    private lateinit var skin: Skin

    override fun show() {
        // Initialize the stage and skin
        stage = Stage()
        skin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage

        // Create a label or other UI elements for the game screen
        val label = Label("Game Started!", skin)
        label.setPosition(100f, 100f)
        stage.addActor(label)
    }

    override fun render(delta: Float) {
        // Clear the screen with a color (optional)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)  // Black background for the game screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Draw the stage (UI elements)
        stage.act(Math.min(Gdx.graphics.deltaTime, 1f / 30f))
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {}

    override fun resume() {}

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
