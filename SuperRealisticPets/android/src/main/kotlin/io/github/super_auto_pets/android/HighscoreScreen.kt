package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.firebase.HighscoreManager

class HighscoreScreen(private val game: Main) : Screen {

    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bgTexture = Texture(Gdx.files.internal("backgrounds/victory_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // --- Root Table ---
        val root = Table()
        root.setFillParent(true)
        root.top().center()
        stage.addActor(root)

        // --- Title Image ---
        val titleTexture = Texture(Gdx.files.internal("buttons/highscore.png"))
        val titleImage = Image(TextureRegionDrawable(TextureRegion(titleTexture)))
        titleImage.setSize(500f, 120f)
        root.add(titleImage).padTop(40f).padBottom(30f)
        root.row()
// --- Box Background ---
        val boxBg = Texture(Gdx.files.internal("backgrounds/box_bg.png"))
        val boxTable = Table()
        boxTable.background = TextureRegionDrawable(TextureRegion(boxBg))
        boxTable.pad(40f)
        root.add(boxTable).width(700f).height(500f).padBottom(30f)
        root.row()

// --- Scores Table (inside box) ---
        val scoresTable = Table()
        val scrollPane = ScrollPane(scoresTable, skin).apply {
            setFadeScrollBars(false)
            setScrollingDisabled(true, false)
            style.background = null
        }
        scoresTable.setBackground(null as Drawable?)
        boxTable.add(scrollPane).expand().fill()



        // --- Populate scores ---
        HighscoreManager.fetchTopHighscores { entries ->
            Gdx.app.postRunnable {
                scoresTable.clear()

                val headerPlayer = Label("Player", skin).apply {
                    setFontScale(2f)
                }
                val headerStreak = Label("Streak", skin).apply {
                    setFontScale(2f)
                }

                scoresTable.add(headerPlayer).expandX().left().padBottom(20f).padLeft(20f)
                scoresTable.add(headerStreak).right().padRight(20f).padBottom(20f)
                scoresTable.row()

                entries.forEach { entry ->
                    val playerLabel = Label(entry.playerName, skin).apply {
                        setFontScale(1.6f)
                    }
                    val scoreLabel = Label(entry.bestStreak.toString(), skin).apply {
                        setFontScale(1.6f)
                    }

                    scoresTable.add(playerLabel).expandX().left().padLeft(30f).padBottom(15f)
                    scoresTable.add(scoreLabel).right().padRight(30f).padBottom(15f)
                    scoresTable.row()
                }
            }
        }

        // --- Exit Button ---
        val exitTexture = Texture(Gdx.files.internal("buttons/exit.png"))
        val exitBtn = ImageButton(TextureRegionDrawable(TextureRegion(exitTexture)))

        val btnW = 400f
        val btnH = 200f
        val verticalPad = 100f

        exitBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
            }
        })

        root.row().padTop(verticalPad * 0.65f)
        root.add(exitBtn).width(btnW * 0.5f).height(btnH * 0.8f).colspan(2).padBottom(30f)

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
    override fun hide() { Gdx.input.inputProcessor = null }
    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
