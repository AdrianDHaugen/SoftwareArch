package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.controller.GameMode

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

        val bg = Texture(Gdx.files.internal("main_menu_bg.png"))
        val bgImg = Image(TextureRegionDrawable(TextureRegion(bg))).apply {
            setSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
            addAction(
                Actions.forever(
                    Actions.sequence(
                        Actions.moveBy(20f, 0f, 30f),
                        Actions.moveBy(-20f, 0f, 30f)
                    )
                )
            )
        }
        stage.addActor(bgImg)

        // Root table fills viewport
        val table = Table(skin).apply {
            setFillParent(true)
            top()
            center()
            padTop(VIRTUAL_HEIGHT * 0.30f)
        }
        stage.addActor(table)

        // Create a table just for the logo
        val logoTable = Table(skin).apply {
            setFillParent(true)
            top()
            padTop(VIRTUAL_HEIGHT * 0.02f)
        }
        stage.addActor(logoTable)

        val logoTexture = Texture(Gdx.files.internal("logo.png"))
        val logoHeight = 612f*1.1f
        val logoWidth = 918f*1.1f

        val logoImg = Image(TextureRegionDrawable(TextureRegion(logoTexture))).apply {
            setSize(logoWidth, logoHeight)
            addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        }

        logoTable.add(logoImg).size(logoWidth, logoHeight).expandX().center()

        // Button sizes
        val btnW = VIRTUAL_WIDTH * 0.25f
        val btnH = VIRTUAL_HEIGHT * 0.25f

        // Load button textures
        val singleTexture = Texture(Gdx.files.internal("singleplayer.png"))
        val multiTexture = Texture(Gdx.files.internal("multiplayer.png"))
        val exitTexture = Texture(Gdx.files.internal("exit.png"))
        val tutorialTexture = Texture(Gdx.files.internal("tutorial.png"))

        val singleBtn = Image(TextureRegionDrawable(TextureRegion(singleTexture))).apply {
            setSize(btnW, btnH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = EditScreen(game, GameMode.SINGLEPLAYER)
                }
            })
        }

        val multiBtn = Image(TextureRegionDrawable(TextureRegion(multiTexture))).apply {
            setSize(btnW, btnH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = EditScreen(game, GameMode.LOCAL_MULTIPLAYER)
                }
            })
        }

        val exitBtn = Image(TextureRegionDrawable(TextureRegion(exitTexture))).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
        }

        val tutorialBtn = Image(TextureRegionDrawable(TextureRegion(tutorialTexture))).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    //game.screen = TutorialScreen(game)
                }
            })
        }
        // Highscore
        val highscoreTexture = Texture(Gdx.files.internal("highscore.png"))

        val highscoreBtn = Image(TextureRegionDrawable(TextureRegion(highscoreTexture))).apply {
            setSize(btnW, btnH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = HighscoreScreen(game)
                }
            })
        }

        // Create a two-column table for better layout
        val buttonTable = Table(skin)
        val verticalPad = -200f // Spacing between rows
        val horizontalPad = -30f // Spacing between buttons

        buttonTable.add(singleBtn).width(btnW*1.1f).height(btnH).padRight(horizontalPad)
        buttonTable.add(multiBtn).width(btnW).height(btnH*1.55f).padLeft(horizontalPad).row()

        buttonTable.row().padTop(verticalPad)
        buttonTable.add(tutorialBtn).width(btnW).height(btnH*1.3f).padRight(horizontalPad)
        buttonTable.add(highscoreBtn).width(btnW).height(btnH*1.1f).padLeft(horizontalPad).row()

        // Third row: Exit button centered
        buttonTable.row().padTop(verticalPad * 0.65f)
        buttonTable.add(exitBtn).width(btnW * 0.5f).height(btnH * 0.8f).colspan(2).padBottom(30f)

        // Add the button table to the main table
        table.add(buttonTable).colspan(2).row()
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
