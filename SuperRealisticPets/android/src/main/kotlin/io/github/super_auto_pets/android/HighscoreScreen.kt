package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.android.ui.enableHoverPop
import io.github.super_auto_pets.firebase.HighscoreManager

class HighscoreScreen(private val game: Main) : Screen {

    companion object {
        private const val VIRTUAL_WIDTH  = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage    = Stage(viewport)
    private val skin     = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bgTex = Texture(Gdx.files.internal("backgrounds/victory_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTex))).apply {
            setSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
        }
        stage.addActor(bgImage)

        // --- Root Table ---
        val root = Table().apply {
            setFillParent(true)
            top().center()
        }
        stage.addActor(root)

        // --- Title Image ---
        val titleTex = Texture(Gdx.files.internal("buttons/highscore.png"))
        val titleImg = Image(TextureRegionDrawable(TextureRegion(titleTex))).apply {
            setSize(500f, 120f)
        }
        root.add(titleImg).padTop(40f).padBottom(30f)
        root.row()

        // --- Scores Box ---
        val boxBg = Texture(Gdx.files.internal("backgrounds/box_bg.png"))
        val boxTable = Table().apply {
            background = TextureRegionDrawable(TextureRegion(boxBg))
            pad(40f)
        }
        root.add(boxTable).width(700f).height(500f).padBottom(30f)
        root.row()

        // --- Scores List ---
        val scoresTable = Table()
        val scrollPane = ScrollPane(scoresTable, skin).apply {
            setFadeScrollBars(false)
            setScrollingDisabled(true, false)
            style.background = null
        }
        boxTable.add(scrollPane).expand().fill()

        HighscoreManager.fetchTopHighscores { entries ->
            Gdx.app.postRunnable {
                scoresTable.clear()
                val hPlayer = Label("Player", skin).apply { setFontScale(2f) }
                val hStreak = Label("Streak", skin).apply { setFontScale(2f) }

                scoresTable.add(hPlayer)
                    .expandX().left().padLeft(20f).padBottom(20f)
                scoresTable.add(hStreak)
                    .right().padRight(20f).padBottom(20f)
                scoresTable.row()

                entries.forEach { e ->
                    val pLbl = Label(e.playerName, skin).apply { setFontScale(1.6f) }
                    val sLbl = Label(e.bestStreak.toString(), skin).apply { setFontScale(1.6f) }
                    scoresTable.add(pLbl)
                        .expandX().left().padLeft(30f).padBottom(15f)
                    scoresTable.add(sLbl)
                        .right().padRight(30f).padBottom(15f)
                    scoresTable.row()
                }
            }
        }
        val verticalPad = 100f
        // --- Exit Button  ---
        val exitTex = Texture(Gdx.files.internal("buttons/exit.png"))

        // compute aspect so we don’t stretch the PNG
        val aspect = exitTex.width.toFloat() / exitTex.height
        val targetW = VIRTUAL_WIDTH * 0.1f    // 10% of screen width;
        val targetH = targetW / aspect

        val exitImg = Image(TextureRegionDrawable(TextureRegion(exitTex))).apply {
            setSize(targetW, targetH)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MainMenuScreen(game)
                }
            })
            enableHoverPop()
        }

        // place it in the table
        root.row().padTop(verticalPad * 0.65f)
        root.add(exitImg)
            .size(targetW, targetH)
            .colspan(2)
            .padBottom(30f)
    }

    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause()  {}
    override fun resume() {}

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
