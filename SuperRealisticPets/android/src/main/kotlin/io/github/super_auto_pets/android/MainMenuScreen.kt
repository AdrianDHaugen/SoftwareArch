package io.github.super_auto_pets.android


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.Screen
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport

class MainMenuScreen(private val game: Main) : Screen {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage
        Gdx.app.log("DEBUG", "File exists? " + Gdx.files.internal("uiskin.json").exists())
        // Create a root table for layout
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        // UI elements
        val titleLabel = Label("Super Realistic Pets", skin, "default")
        val singlePlayerBtn = TextButton("Play Game!", skin)

        // Button click -> go to another screen
        singlePlayerBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = EditScreen(game)
            }
        })

        // Layout with table
        table.add(titleLabel).colspan(3).pad(10f).row()
        table.row().pad(10f)
        table.add(singlePlayerBtn).width(150f).padRight(10f)
    }

    override fun render(delta: Float) {
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
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}

