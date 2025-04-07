package io.github.some_example_name.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.InputEvent

class MainMenuScreen(private val game: GameManager) : Screen {
    private lateinit var stage: Stage
    private lateinit var uiSkin: Skin
    private lateinit var table: Table

    override fun show() {
        // Initialize stage and skin
        stage = Stage()
        uiSkin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage

        // Create a table and set it to fill the parent (screen)
        table = Table()
        table.debug = true
        table.setFillParent(true)
        stage.addActor(table)

        // Create a button that changes the screen to the game screen
        val startButton = TextButton("Start Game", uiSkin)
        startButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = GameScreen(game)  // Switch to the GameScreen
            }
        })
        // Add the button to the table
        table.add(startButton).fillX().uniformX().pad(10f)
        table.row()  // Move to the next row in the table
        table.center()
    }

    override fun render(delta: Float) {
        // Clear the screen with a color (optional)
        Gdx.gl.glClearColor(0f, 1f, 0f, 1f)  // Green background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Draw the stage (UI elements)
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        // Initialize the stage if it wasn't done already in show()
        if (!::stage.isInitialized) {
            stage = Stage()
        }

        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        stage.dispose()
    }

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        stage.dispose()
        uiSkin.dispose()
    }
}
