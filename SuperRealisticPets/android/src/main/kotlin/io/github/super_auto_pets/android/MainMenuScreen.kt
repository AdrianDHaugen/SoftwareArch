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

        // Background spans full virtual world
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
        }
        stage.addActor(table)

        // Title label scaled relative to virtual width
        val title = Label("Super Realistic Pets", skin).apply {
            setFontScale(VIRTUAL_WIDTH / 800f) // scales font based on resolution
            addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        }
        title.setFontScale(7f)
        table.add(title).colspan(1).padBottom(30f).row()

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


// Add to table
        val verticalPad = 20f
        val buttonHeight = btnH * 1.2f
        val buttonWidth = btnW

        table.add(singleBtn).width(buttonWidth).height(buttonHeight).padBottom(verticalPad).row()
        table.add(multiBtn).width(buttonWidth).height(buttonHeight).padBottom(verticalPad).row()
        table.add(tutorialBtn).width(buttonWidth).height(buttonHeight).padBottom(verticalPad).row()
        table.add(highscoreBtn).width(buttonWidth).height(buttonHeight).padBottom(verticalPad).row()
        table.add(exitBtn).width(buttonWidth * 0.6f).height(buttonHeight * 0.8f).padTop(verticalPad).row()



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
