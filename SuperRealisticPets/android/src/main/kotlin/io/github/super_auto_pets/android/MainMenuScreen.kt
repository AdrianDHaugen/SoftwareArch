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

/**
 * Main menu screen for Super Realistic Pets.
 * Manages the layout and interaction of the main menu UI elements.
 */
class MainMenuScreen(private val game: Main) : Screen {
    companion object {
        // Define a fixed virtual resolution for consistent layout
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f

        // UI constants
        private const val LOGO_PADDING_TOP = 0.02f
        private const val MENU_PADDING_TOP = 0.30f
        private const val BUTTON_VERTICAL_PADDING = -200f
        private const val BUTTON_HORIZONTAL_PADDING = -30f
    }

    // UI components
    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    // Asset tracking for disposal
    private val textures = mutableListOf<Texture>()

    override fun show() {
        Gdx.input.inputProcessor = stage
        setupBackground()
        setupLogo()
        setupButtons()
    }

    private fun setupBackground() {
        val bgTexture = loadTexture("main_menu_bg.png")
        val bgImg = Image(TextureRegionDrawable(TextureRegion(bgTexture))).apply {
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
    }

    private fun setupLogo() {
        val logoTable = Table(skin).apply {
            setFillParent(true)
            top()
            padTop(VIRTUAL_HEIGHT * LOGO_PADDING_TOP)
        }
        stage.addActor(logoTable)

        val logoTexture = loadTexture("logo.png")
        val logoHeight = 612f * 1.1f
        val logoWidth = 918f * 1.1f

        val logoImg = Image(TextureRegionDrawable(TextureRegion(logoTexture))).apply {
            setSize(logoWidth, logoHeight)
            addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)))
        }

        logoTable.add(logoImg).size(logoWidth, logoHeight).expandX().center()
    }

    private fun setupButtons() {
        // Root table for menu layout
        val menuTable = Table(skin).apply {
            setFillParent(true)
            top()
            center()
            padTop(VIRTUAL_HEIGHT * MENU_PADDING_TOP)
        }
        stage.addActor(menuTable)

        // Button sizes
        val btnWidth = VIRTUAL_WIDTH * 0.25f
        val btnHeight = VIRTUAL_HEIGHT * 0.25f

        // Create and add button table to the main menu table
        val buttonTable = createButtonTable(btnWidth, btnHeight)
        menuTable.add(buttonTable).colspan(2).row()
    }

    private fun createButtonTable(btnWidth: Float, btnHeight: Float): Table {
        val buttonTable = Table(skin)

        // First row: Singleplayer and Multiplayer buttons
        addSingleplayerButton(buttonTable, btnWidth, btnHeight)
        addMultiplayerButton(buttonTable, btnWidth, btnHeight)
        buttonTable.row().padTop(BUTTON_VERTICAL_PADDING)

        // Second row: Tutorial and Highscore buttons
        addTutorialButton(buttonTable, btnWidth, btnHeight)
        addHighscoreButton(buttonTable, btnWidth, btnHeight)
        buttonTable.row().padTop(BUTTON_VERTICAL_PADDING * 0.65f)

        // Third row: Exit button (centered)
        addExitButton(buttonTable, btnWidth, btnHeight)

        return buttonTable
    }

    private fun addSingleplayerButton(table: Table, btnWidth: Float, btnHeight: Float) {
        val texture = loadTexture("singleplayer.png")
        val button = createMenuButton(texture, btnWidth * 1.1f, btnHeight) {
            game.screen = EditScreen(game, GameMode.SINGLEPLAYER)
        }
        table.add(button).width(btnWidth * 1.1f).height(btnHeight).padRight(BUTTON_HORIZONTAL_PADDING)
    }

    private fun addMultiplayerButton(table: Table, btnWidth: Float, btnHeight: Float) {
        val texture = loadTexture("multiplayer.png")
        val button = createMenuButton(texture, btnWidth, btnHeight * 1.55f) {
            game.screen = EditScreen(game, GameMode.LOCAL_MULTIPLAYER)
        }
        table.add(button).width(btnWidth).height(btnHeight * 1.55f).padLeft(BUTTON_HORIZONTAL_PADDING)
    }

    private fun addTutorialButton(table: Table, btnWidth: Float, btnHeight: Float) {
        val texture = loadTexture("tutorial.png")
        val button = createMenuButton(texture, btnWidth, btnHeight * 1.3f) {
             game.screen = TutorialScreen(game)
        }
        table.add(button).width(btnWidth).height(btnHeight * 1.3f).padRight(BUTTON_HORIZONTAL_PADDING)
    }

    private fun addHighscoreButton(table: Table, btnWidth: Float, btnHeight: Float) {
        val texture = loadTexture("highscore.png")
        val button = createMenuButton(texture, btnWidth, btnHeight * 1.1f) {
            game.screen = HighscoreScreen(game)
        }
        table.add(button).width(btnWidth).height(btnHeight * 1.1f).padLeft(BUTTON_HORIZONTAL_PADDING)
    }

    private fun addExitButton(table: Table, btnWidth: Float, btnHeight: Float) {
        val texture = loadTexture("exit.png")
        val button = createMenuButton(texture, btnWidth * 0.5f, btnHeight * 0.8f) {
            Gdx.app.exit()
        }
        table.add(button).width(btnWidth * 0.5f).height(btnHeight * 0.8f).colspan(2).padBottom(30f)
    }

    /**
     * Creates a menu button with the given texture and click handler
     */
    private fun createMenuButton(texture: Texture, width: Float, height: Float, onClick: () -> Unit): Image {
        return Image(TextureRegionDrawable(TextureRegion(texture))).apply {
            setSize(width, height)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    onClick()
                }
            })
        }
    }

    /**
     * Loads a texture and tracks it for disposal
     */
    private fun loadTexture(path: String): Texture {
        val texture = Texture(Gdx.files.internal(path))
        textures.add(texture)
        return texture
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
        // Dispose all tracked textures
        textures.forEach { it.dispose() }
        textures.clear()
    }
}
