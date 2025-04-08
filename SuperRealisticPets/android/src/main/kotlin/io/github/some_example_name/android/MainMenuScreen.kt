package io.github.some_example_name.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport

class MainMenuScreen(private val game: GameManager) : Screen {
    private val stage = Stage(FitViewport(800f, 480f))
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage

        // Create a root table for layout
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // UI elements
        val titleLabel = Label("Super Realistic Pets", skin, "default")
        val singlePlayerBtn = TextButton("Single Player", skin)
        val multiPlayerBtn = TextButton("Multi Player", skin)
        val settingsBtn = TextButton("Settings", skin)

        // Button click -> go to another screen
        /*
        singlePlayerBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = TeamManagementScreen(game)
            }
        })
         */

        // Layout with table
        table.add(titleLabel).colspan(3).pad(10f).row()
        table.row().pad(10f)
        table.add(singlePlayerBtn).width(150f).padRight(10f)
        table.add(multiPlayerBtn).width(150f).padRight(10f)
        table.add(settingsBtn).width(150f)
    }

    override fun render(delta: Float) {
        // Clear screen
        Gdx.gl.glClearColor(0.192f, 0.341f, 0.659f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update/draw stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun hide() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}

