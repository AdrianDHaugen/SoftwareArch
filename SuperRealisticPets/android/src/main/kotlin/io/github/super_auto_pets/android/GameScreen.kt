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
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import io.github.super_auto_pets.firebase.FirebaseHighscoreService


class GameScreen(private val game: Main) : Screen {

    /**
    class GameScreen(
}
            private val game: Main,
            private val battleController: BattleController
        ) : Screen {
    */

    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    //For testing, should be removed
    private lateinit var battleController: BattleController

    private val cellPositions = mutableListOf<Pair<Float,Float>>()

    // Nine-slot array for the battle field.
    private val battleFieldActors: MutableList<Image?> = MutableList(9) { null }
    private lateinit var battleFieldTable: Table

    // The Next Attack button.
    private lateinit var nextAttackButton: TextButton

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- compute the 9 “slots” in a row ---
        val cellW = stage.viewport.worldWidth / 9f
        // pick a Y that centers your 150×150 images vertically:
        val cellY = stage.viewport.worldHeight/2f - 75f
        for (i in 0 until 9) {
            // center each 150px image in its cell
            val x = i*cellW + (cellW - 150f)/2
            cellPositions.add(x to cellY)
        }

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
        // wipe out any old Images
        battleFieldActors.forEach { it?.remove() }
        battleFieldActors.clear()

        // pull from your controller exactly the same way…
        val teamLeft  = battleController.battle.playerA.team.teams.filterIsInstance<Sprite>()
        val teamRight = battleController.battle.playerB.team.teams.filterIsInstance<Sprite>()

        // build a 9‑slot list of Sprites (or null)
        val slots = MutableList<Sprite?>(9) { null }
        teamLeft .take(4).forEachIndexed { i, s -> slots[3 - i] = s }
        teamRight.take(4).forEachIndexed { i, s -> slots[5 + i] = s }

        // now place each Sprite’s Image at the precalculated x,y
        slots.forEachIndexed { idx, sprite ->
            if (sprite != null) {
                val img = createPetImage(sprite)
                val (x,y) = cellPositions[idx]
                img.setPosition(x, y)
                stage.addActor(img)
                battleFieldActors.add(img)
            } else {
                battleFieldActors.add(null)
            }
        }
    }

    //think this needs to be changed when connecting to the shop
    private val texCache = mutableMapOf<String, Texture>()

    private fun createPetImage(sprite: Sprite): Image {
        val file = when (sprite.name) {
            "cat"  -> "cat-1-base-nb.PNG"
            "dog"  -> "dog-1-base-nb.PNG"
            "bird" -> "bird-1-base-nb.PNG"
            "fish" -> "fish-1-base-nb.PNG"
            else   -> "heart.png"
        }
        val tex = texCache.getOrPut(file) {
            Texture(Gdx.files.internal(file))
        }
        return Image(TextureRegionDrawable(TextureRegion(tex))).apply {
            setSize(200f, 200f)
            userObject = sprite
        }
    }

    /**
     * Called when the user presses the Next Attack button.
     * Processes one battle step.
     */
    private fun performBattleStep() {
        // 1) Disable the button to prevent spamming
        nextAttackButton.isDisabled = true

        // 2) Process one battle step
        val event = battleController.nextAttackStep()
        if (event == null) {
            println("Battle is over!")
            return
        }

        // 3) Find UI actors for attacker/defender
        val attackerActor = findUIActorFor(event.attacker)
        val defenderActor = findUIActorFor(event.defender)

        // 4) Timing parameters
        val moveDist = 100f
        val moveDur  = 0.3f
        val flashDur = 0.2f
        val fadeDur  = 0.5f

        // 5) Build wiggle sequences
        val baseSeq = Actions.sequence(
            Actions.moveBy(moveDist, 0f, moveDur, Interpolation.sine),
            Actions.color(Color.RED,   flashDur, Interpolation.fade),
            Actions.color(Color.WHITE, flashDur, Interpolation.fade),
            Actions.moveBy(-moveDist, 0f, moveDur, Interpolation.sine)
        )
        val revSeq = Actions.sequence(
            Actions.moveBy(-moveDist, 0f, moveDur, Interpolation.sine),
            Actions.color(Color.RED,   flashDur, Interpolation.fade),
            Actions.color(Color.WHITE, flashDur, Interpolation.fade),
            Actions.moveBy(moveDist, 0f, moveDur, Interpolation.sine)
        )

        // 6) Helper to append fade-out if the sprite died this round
        fun wrapWithFade(seq: com.badlogic.gdx.scenes.scene2d.Action, died: Boolean) =
            if (died) Actions.sequence(seq, Actions.fadeOut(fadeDur)) else seq

        // 7) Apply actions
        attackerActor?.addAction(wrapWithFade(baseSeq, event.diedSprites.contains(event.attacker)))
        defenderActor?.addAction(wrapWithFade(revSeq,  event.diedSprites.contains(event.defender)))

        // 8) Schedule post-animation updates
        val totalTime = moveDur*2 + flashDur*2 + fadeDur
        stage.addAction(
            Actions.sequence(
                Actions.delay(totalTime),
                Actions.run {
                    // 8a) Remove dead actors
                    event.diedSprites.forEach { dead ->
                        findUIActorFor(dead)?.remove()
                    }

                    // 8b) Slide survivors into their new slots
                    val moveTime = 0.5f
                    // Left team: slots 3→2→1→0
                    battleController.battle.playerA.team.teams
                        .filterIsInstance<Sprite>()
                        .take(4)
                        .forEachIndexed { i, sprite ->
                            val targetSlot = 3 - i
                            findUIActorFor(sprite)?.addAction(
                                Actions.moveTo(
                                    cellPositions[targetSlot].first,
                                    cellPositions[targetSlot].second,
                                    moveTime,
                                    Interpolation.sine
                                )
                            )
                        }
                    // Right team: slots 5→6→7→8
                    battleController.battle.playerB.team.teams
                        .filterIsInstance<Sprite>()
                        .take(4)
                        .forEachIndexed { i, sprite ->
                            val targetSlot = 5 + i
                            findUIActorFor(sprite)?.addAction(
                                Actions.moveTo(
                                    cellPositions[targetSlot].first,
                                    cellPositions[targetSlot].second,
                                    moveTime,
                                    Interpolation.sine
                                )
                            )
                        }

                    // 8c) Re-enable button if the battle still has fighters
                    val leftAlive = battleController.battle.playerA.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }
                    val rightAlive = battleController.battle.playerB.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }
                    nextAttackButton.isDisabled = !(leftAlive && rightAlive)
                }
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
        val highscoreService = FirebaseHighscoreService()
        val bc = BattleController(highscoreService = highscoreService)


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

