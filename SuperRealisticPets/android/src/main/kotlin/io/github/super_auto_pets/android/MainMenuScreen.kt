package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport

class MainMenuScreen(private val game: Main) : Screen {
    // Define a fixed virtual resolution for consistent layout
    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Background spans full virtual world
        val bg = Texture(Gdx.files.internal("main_menu_bg.png"))
        val bgImg = Image(TextureRegionDrawable(TextureRegion(bg))).apply {
            setSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
            addAction(Actions.forever(
                Actions.sequence(
                    Actions.moveBy(20f, 0f, 30f),
                    Actions.moveBy(-20f, 0f, 30f)
                )
            ))
        }
        stage.addActor(bgImg)

        // Root table fills viewport
        val table = Table(skin).apply {
            setFillParent(true)
            top()
            center()
            defaults().pad(VIRTUAL_HEIGHT * 0.02f)
        }
        stage.addActor(table)

        // Title label scaled relative to virtual width
        val title = Label("Super Realistic Pets", skin).apply {
            setFontScale(VIRTUAL_WIDTH / 800f) // scales font based on resolution
            addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        }
        table.add(title).colspan(1).row()

        // Button sizes relative to virtual dimensions
        val btnW = VIRTUAL_WIDTH * 0.25f
        val btnH = VIRTUAL_HEIGHT * 0.1f

        // Single player
        val singleBtn = TextButton("Single Player", skin).apply {
            label.setFontScale(VIRTUAL_WIDTH / 1200f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = EditScreen(game)
                }
            })
        }
        table.add(singleBtn).width(btnW).height(btnH).row()

        // Multiplayer
        val multiBtn = TextButton("Multiplayer", skin).apply {
            label.setFontScale(VIRTUAL_WIDTH / 1200f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    // TODO: Multiplayer screen transition
                }
            })
        }
        table.add(multiBtn).width(btnW).height(btnH).row()

        // Exit
        val exitBtn = TextButton("Exit", skin).apply {
            label.setFontScale(VIRTUAL_WIDTH / 1600f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
        }
        table.add(exitBtn).width(btnW * 0.5f).height(btnH * 0.75f)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
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
