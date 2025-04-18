package io.github.super_auto_pets.android

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.models.Battle

class Main : Game() {

    //lateinit var batch: SpriteBatch
    //lateinit var font: BitmapFont
    lateinit var battleController : BattleController

    override fun create() {
        Gdx.app.log("DEBUG", "Main().create() runs!")
        var battle = Battle()
        battleController = BattleController(battle)


        // Set the initial screen
        setScreen(MainMenuScreen(this))  // Set the main menu screen as the initial screen
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.render()  // This renders the current screen
    }

    override fun dispose() {
        //batch.dispose()
        //font.dispose()
        super.dispose()
    }
}
