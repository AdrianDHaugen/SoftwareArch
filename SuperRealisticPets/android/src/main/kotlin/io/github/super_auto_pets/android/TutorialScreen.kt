package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport

class TutorialScreen(private val game: Main) : Screen {

    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Background image
        val bgTexture = Texture(Gdx.files.internal("backgrounds/tutorial_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // Root table
        val root = Table(skin).apply {
            setFillParent(true)
            top().center()
            padTop(50f)
        }
        stage.addActor(root)

        // Box background for tutorial text (same as HighscoreScreen)
        val boxBg = Texture(Gdx.files.internal("backgrounds/box_bg.png"))
        val boxTable = Table().apply {
            background = TextureRegionDrawable(TextureRegion(boxBg))
            pad(40f)
        }

        // Tutorial text content
        val tutorialText = """
            Welcome to Super Realistic Pets!

            1. Shop Phase
               • You start each turn with 10 gold.
               • Buy pets and optionally items.

            2. Pet Stats
               • Attack: damage dealt each round.
               • Health: survivability—pets die at 0 HP.

            3. Items
               • Equip to pets for bonus stats.

            4. Battle Phase
               • Battles play out front‑to‑back.
               • Click Step-By-Step to manually click through battle
               • Click Auto Play to fast forward though battle

            5. How to the Game
               • Survive to win.

            Good luck, may the best pets win!
        """

        // Content table and scroll pane
        val content = Table()
        val label = Label(tutorialText, skin).apply {
            setFontScale(VIRTUAL_WIDTH / 1000f)
            setWrap(true)
        }
        content.add(label).expand().fill().padRight(20f)

        val scroll = ScrollPane(content, skin).apply {
            setFadeScrollBars(false)
            setScrollingDisabled(true, false)
            style.background = null
        }
        boxTable.add(scroll).expand().fill()

        // Add boxed text to root
        root.add(boxTable).width(VIRTUAL_WIDTH * 0.8f).height(VIRTUAL_HEIGHT * 0.6f)
        root.row().padTop(30f)

        // Back button
        val btnW = VIRTUAL_WIDTH * 0.15f
        val btnH = VIRTUAL_HEIGHT * 0.15f
        val backTexture = Texture(Gdx.files.internal("backgrounds/back.png"))
        val backBtn = Image(TextureRegionDrawable(TextureRegion(backTexture))).apply {
            setSize(btnW, btnH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MainMenuScreen(game)
                }
            })
        }
        root.add(backBtn).width(btnW).height(btnH)
    }

    override fun render(delta: Float) {
        stage.act(delta)
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
