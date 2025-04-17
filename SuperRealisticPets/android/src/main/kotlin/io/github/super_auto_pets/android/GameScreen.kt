package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
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
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.models.Sprite

class GameScreen(private val game: Main) : Screen {

    /**
    class GameScreen(
}
            private val game: Main,
            private val battleController: BattleController
        ) : Screen {
    */

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    //For testing, should be removed
    private lateinit var battleController: BattleController

    // Nine-slot array for the battle field.
    private val battleFieldActors: MutableList<Image?> = MutableList(9) { null }
    private lateinit var battleFieldTable: Table

    // The Next Attack button.
    private lateinit var nextAttackButton: TextButton

    override fun show() {
        stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
        skin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage

        // Background
        val bgTexture = Texture(Gdx.files.internal("battle_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // Initialize battle scenario, remove when connecting to shop stage
        battleController = createTestBattle()

        // Set up battle field table (9 fixed cells)
        battleFieldTable = Table(skin)
        battleFieldTable.setFillParent(true)
        stage.addActor(battleFieldTable)
        val cellWidth = stage.viewport.worldWidth / 9f
        for (i in 0 until 9) {
            battleFieldTable.add().width(cellWidth).height(220f).pad(10f)
        }
        battleFieldTable.row()

        // Populate initial UI based on the current model state.
        refreshBattleFieldUI()

        // Add a Next Attack button
        nextAttackButton = TextButton("Next Attack", skin)
        nextAttackButton.addListener(object : com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent, x: Float, y: Float) {
                performBattleStep()
            }
        })
        // Add the button to the stage
        val buttonTable = Table(skin)
        buttonTable.setFillParent(true)
        buttonTable.bottom().center()
        buttonTable.add(nextAttackButton).pad(20f)
        stage.addActor(buttonTable)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    override fun hide() { stage.dispose() }
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }

    /**
     * Rebuilds the battle field UI from the current model state.
     * For team left, maps teamLeft[0] → slot 3, teamLeft[1] → slot 2, etc.
     * For team right, maps teamRight[0] → slot 5, teamRight[1] → slot 6, etc.
     * Slot 4 remains empty.
     */
    private fun refreshBattleFieldUI() {
        for (i in 0 until 9) {
            battleFieldActors[i] = null
        }

        val teamLeft = battleController.battle.playerA.team.teams.filterIsInstance<Sprite>()
        for (i in teamLeft.indices) {
            if (i > 3) break
            val battleIndex = 3 - i
            battleFieldActors[battleIndex] = createPetImage(teamLeft[i])
        }

        val teamRight = battleController.battle.playerB.team.teams.filterIsInstance<Sprite>()
        for (i in teamRight.indices) {
            if (i > 3) break
            val battleIndex = 5 + i
            battleFieldActors[battleIndex] = createPetImage(teamRight[i])
        }

        battleFieldTable.clearChildren()
        val cellWidth = stage.viewport.worldWidth / 9f
        for (i in 0 until 9) {
            if (battleFieldActors[i] != null) {
                battleFieldTable.add(battleFieldActors[i]).size(150f, 150f).pad(10f)
            } else {
                battleFieldTable.add().width(cellWidth).height(220f).pad(10f)
            }
        }
        battleFieldTable.row()
    }
    //think this needs to be changed when connecting to the shop
    private fun createPetImage(sprite: Sprite): Image {
        val textureFile = when (sprite.name) {
            "cat" -> "cat-1-base-nb.PNG"
            "dog" -> "dog-1-base-nb.PNG"
            "bird"  -> "bird-1-base-nb.PNG"
            "fish" -> "fish-1-base-nb.PNG"
            else -> {"heart.png"}
        }
        val tex = Texture(Gdx.files.internal(textureFile))
        val img = Image(TextureRegionDrawable(TextureRegion(tex)))
        img.setSize(150f, 150f)
        img.userObject = sprite
        return img
    }

    /**
     * Called when the user presses the Next Attack button.
     * Processes one battle step.
     */
    private fun performBattleStep() {
        val event = battleController.nextAttackStep()
        if (event == null) {
            println("Battle is over!")
            nextAttackButton.isDisabled = true
            return
            }
        // Animate the attacker and defender.
        val attackerActor = findUIActorFor(event.attacker)
        val defenderActor = findUIActorFor(event.defender)
        val attackerAction = Actions.sequence(
            Actions.moveBy(100f, 0f, 0.25f),
            Actions.sequence(
                Actions.color(Color.RED, 0.3f),
                Actions.color(Color.WHITE, 0.3f)
            ),
            Actions.moveBy(-100f, 0f, 0.25f)
        )
        val defenderAction = Actions.sequence(
            Actions.moveBy(-100f, 0f, 0.25f),
            Actions.sequence(
                Actions.color(Color.RED, 0.3f),
                Actions.color(Color.WHITE, 0.3f)
            ),
            Actions.moveBy(100f, 0f, 0.25f)
        )
        attackerActor?.addAction(attackerAction)
        defenderActor?.addAction(defenderAction)

        // After animations finish, update the UI.
        stage.addAction(
            Actions.sequence(
                Actions.delay(1.0f),
                Actions.run { refreshBattleFieldUI() }
            )
        )
    }

    /**
     * Finds the UI actor associated with a given sprite.
     */
    private fun findUIActorFor(sprite: Sprite): Image? {
        return battleFieldActors.find { it?.userObject == sprite } ?: null
    }

    /**
     * A minimal test scenario: each team gets up to four sprites. Remove
     */
    private fun createTestBattle(): BattleController {
        val bc = BattleController()

        // Team Left (playerA) – shop order is 0..3; assign in reverse.
        val catA = Sprite().apply { name = "cat"; health = 10; attack = 2 }
        val dogA = Sprite().apply { name = "dog"; health = 5; attack = 2 }
        val birdA = Sprite().apply { name = "bird"; health = 5; attack = 2 }
        val fishA = Sprite().apply { name = "fish"; health = 5; attack = 2 }
        bc.battle.playerA.team.teams.addAll(listOf(catA, dogA, birdA, fishA))

        // Team Right (playerB) – shop order 0..3; assignment is natural.
        val catB = Sprite().apply { name = "cat"; health = 10; attack = 3 }
        val dogB = Sprite().apply { name = "dog"; health = 5; attack = 2 }
        val birdB = Sprite().apply { name = "bird"; health = 5; attack = 2 }
        val fishB = Sprite().apply { name = "fish"; health = 5; attack = 2 }
        bc.battle.playerB.team.teams.addAll(listOf(catB, dogB, birdB, fishB))

        return bc
    }
}

