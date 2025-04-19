package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.firebase.HighscoreManager

class HighscoreScreen(private val game: Main) : Screen {

    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bgTexture = Texture(Gdx.files.internal("victory_bg.png")) // or create a new bg like "highscore_bg.png"
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // --- Root Table ---
        val table = Table(skin)
        table.setFillParent(true)
        table.center()
        stage.addActor(table)

        val title = Label("🏆 Highscores", skin)
        title.setFontScale(2f)
        table.add(title).colspan(2).pad(20f)
        table.row()

        val loading = Label("Loading highscores...", skin)
        table.add(loading).colspan(2).pad(10f)
        table.row()

        // --- Fetch and display highscores from Firebase ---
        HighscoreManager.fetchTopHighscores { entries ->
            Gdx.app.postRunnable {
                table.clear()
                table.add(Label("Player", skin)).pad(10f)
                table.add(Label("Best Streak", skin)).pad(10f)
                table.row()

                entries.forEach {
                    table.add(Label(it.playerName, skin)).pad(8f)
                    table.add(Label(it.bestStreak.toString(), skin)).pad(8f)
                    table.row()
                }

                // Back to main menu
                val backButton = TextButton("Back", skin)
                backButton.addListener(object : ClickListener() {
                    override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                        game.screen = MainMenuScreen(game)
                    }
                })

                table.row().padTop(30f)
                table.add(backButton).colspan(2).width(300f).height(100f).pad(10f)
            }
        }
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
