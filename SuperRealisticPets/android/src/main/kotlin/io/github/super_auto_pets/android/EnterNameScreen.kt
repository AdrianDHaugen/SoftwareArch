package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.controller.GameMode
import io.github.super_auto_pets.managers.PlayerManager

class EnterNameScreen(
    private val game: Main,
    private val gameMode: GameMode
) : Screen {

    private val viewport = FitViewport(1920f, 1080f)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))


    private lateinit var nameFieldA: TextField
    private lateinit var nameFieldB: TextField

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bgTexture = Texture(Gdx.files.internal("backgrounds/main_menu_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture))).apply {
            setSize(viewport.worldWidth, viewport.worldHeight)
        }
        stage.addActor(bgImage)

        // --- Custom TextField Style ---
        val inputTexture = Texture(Gdx.files.internal("buttons/input_field.png"))
        val inputDrawable = TextureRegionDrawable(TextureRegion(inputTexture))
        val textFieldStyle = TextField.TextFieldStyle().apply {
            background = inputDrawable
            font = BitmapFont().apply {
                data.setScale(4f)
            }
            fontColor = Color.BLACK
            cursor = skin.getDrawable("cursor")
        }

        // --- Layout Table ---
        val table = Table().apply {
            setFillParent(true)
            center()
        }
        stage.addActor(table)

        // --- Title ---
        val title = Label("Enter Player Name${if (gameMode == GameMode.LOCAL_MULTIPLAYER) "s" else ""}", skin).apply {
            setFontScale(5f)
        }
        table.add(title).colspan(2).padBottom(60f)
        table.row()

        // --- Player A ---
        nameFieldA = TextField("", textFieldStyle).apply {
            alignment = 1 // Center text
        }
        table.add(nameFieldA).width(700f).height(150f).colspan(2).padBottom(40f)
        table.row()

        // --- Player B (Only for Multiplayer) ---
        if (gameMode == GameMode.LOCAL_MULTIPLAYER) {
            nameFieldB = TextField("", textFieldStyle).apply {
                alignment = 1
            }
            table.add(nameFieldB).width(700f).height(150f).colspan(2).padBottom(40f)
            table.row()
        }

        // --- Continue Button ---
        val continueButtonTexture = Texture(Gdx.files.internal("buttons/continue.png"))
        val continueButton = ImageButton(TextureRegionDrawable(TextureRegion(continueButtonTexture))).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    val nameA = nameFieldA.text.trim()
                    val nameB = if (this@EnterNameScreen::nameFieldB.isInitialized) nameFieldB.text.trim() else ""

                    if (nameA.isNotEmpty() && (gameMode == GameMode.SINGLEPLAYER || nameB.isNotEmpty())) {
                        PlayerManager.playerAName = nameA
                        if (gameMode == GameMode.LOCAL_MULTIPLAYER) {
                            PlayerManager.playerBName = nameB
                        }
                        game.screen = EditScreen(game, gameMode)
                    }
                }
            })
        }

        table.add(continueButton)
            .width(500f)
            .height(500f)
            .colspan(2)
            .padTop(20f)

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
