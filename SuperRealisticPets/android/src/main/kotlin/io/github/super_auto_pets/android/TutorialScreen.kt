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


    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        val bgTexture = Texture(Gdx.files.internal("tutorial_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        val table = Table(skin).apply {
            setFillParent(true)
            top().padTop(50f)
            defaults().pad(10f)
        }
        stage.addActor(table)

        // Tutorial text
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
               • Click “Next Attack” to step through each attack.

            5. How to the Game
               • Survive to win.

            Good luck, may the best pets win!
        """

        val label = Label(tutorialText, skin).apply {
            setFontScale(VIRTUAL_WIDTH/1000f)
        }

        table.add(label)
        table.row()

        // Button sizes
        val btnW = VIRTUAL_WIDTH * 0.15f
        val btnH = VIRTUAL_HEIGHT * 0.15f

        // Load button textures
        val backTexture = Texture(Gdx.files.internal("back.png"))

        val backBtn = Image(TextureRegionDrawable(TextureRegion(backTexture))).apply {
            setSize(btnW, btnH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MainMenuScreen(game)
                }
            })
        }

        table.add(backBtn).width(btnW).height(btnH).pad(-50f)

    }

    override fun render(delta: Float) {
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


}
