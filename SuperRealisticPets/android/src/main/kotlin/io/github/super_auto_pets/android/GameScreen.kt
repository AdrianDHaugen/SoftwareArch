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
import io.github.super_auto_pets.controller.GameMode

/**
class GameScreen(
}
private val game: Main,
private val gameMode: GameMode,
private val teamA: List<Sprite> = emptyList(),
private val teamB: List<Sprite> = emptyList()
) : Screen {
 */
class GameScreen(
    private val game: Main,
    private val gameMode: GameMode,
    private val teamA: List<Sprite> = emptyList(),
    private val teamB: List<Sprite> = emptyList()
    ) : Screen {


    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    private val heartTexture = Texture(Gdx.files.internal("heart.png"))
    private val swordTexture = Texture(Gdx.files.internal("crossed_swords.png"))
    private val statTableMap = mutableMapOf<Sprite, Table>()
    private lateinit var battleController: BattleController

    private val cellPositions = mutableListOf<Pair<Float,Float>>()

    // Nine-slot array for the battle field.
    private val battleFieldActors: MutableList<Image?> = MutableList(9) { null }
    private lateinit var battleFieldTable: Table

    // The Next Attack button.
    private lateinit var nextAttackButton: TextButton

    override fun show() {
        Gdx.input.inputProcessor = stage

        // --- compute the 9 "slots" in a row ---
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
        battleController = createBattleFromTeams()

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
                if (nextAttackButton.isDisabled) return
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

    private fun refreshBattleFieldUI() {
        // 1) remove old pet Images…
        battleFieldActors.forEach { it?.remove() }
        battleFieldActors.clear()

        // 2) …and also toss any old stat‐tables
        statTableMap.values.forEach { it.remove() }
        statTableMap.clear()

        // 3) pull from your controller exactly the same way…
        val teamLeft  = battleController.battle.playerA.team.teams.filterIsInstance<Sprite>()
        val teamRight = battleController.battle.playerB.team.teams.filterIsInstance<Sprite>()

        // 4) build a 9‑slot list of Sprites (or null)
        val slots = MutableList<Sprite?>(9) { null }
        teamLeft .take(4).forEachIndexed { i, s -> slots[3 - i] = s }
        teamRight.take(4).forEachIndexed { i, s -> slots[5 + i] = s }

        // 5) now place each Sprite's Image (and stats table) at the precalculated x,y
        slots.forEachIndexed { idx, sprite ->
            if (sprite != null) {
                // — Image
                val img = createPetImage(sprite)
                val (x, y) = cellPositions[idx]
                img.setPosition(x, y)
                stage.addActor(img)
                battleFieldActors.add(img)

                // — Stats Table (♥ HP   ⚔ ATK), positioned just under the 200×200 sprite
                val statT = Table().apply { userObject = sprite }
                // heart icon + HP
                statT.add(Image(TextureRegionDrawable(TextureRegion(heartTexture))))
                    .size(32f).padRight(4f)
                val hpLbl = Label(sprite.health.toString(), skin).apply { setFontScale(1f) }
                statT.add(hpLbl).padRight(12f)
                // sword icon + ATK
                statT.add(Image(TextureRegionDrawable(TextureRegion(swordTexture))))
                    .size(32f).padRight(4f)
                val atkLbl = Label(sprite.attack.toString(), skin).apply { setFontScale(1f) }
                statT.add(atkLbl)

                // layout & position
                statT.pack()
                val statX = x + (200f - statT.width) / 2f
                val statY = y - statT.height - 8f
                statT.setPosition(statX, statY)
                stage.addActor(statT)

                // remember it so we can animate & update later
                statTableMap[sprite] = statT
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
            showBattleResult()
            return
        }

        // 3) Find UI actors for attacker/defender (images + stats tables)
        val attackerActor = findUIActorFor(event.attacker)
        val defenderActor = findUIActorFor(event.defender)
        val attackerStats = statTableMap[event.attacker]
        val defenderStats = statTableMap[event.defender]

        // 4) Timing parameters
        val moveDist = 100f
        val moveDur  = 0.3f
        val flashDur = 0.2f
        val fadeDur  = 0.5f

        // 5) Build wiggle sequences for images (with color flash)
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
        fun wrapWithFade(seq: Action, died: Boolean) =
            if (died) Actions.sequence(seq, Actions.fadeOut(fadeDur)) else seq

        // 7) Apply actions to pet images
        attackerActor?.addAction(wrapWithFade(baseSeq, event.diedSprites.contains(event.attacker)))
        defenderActor?.addAction(wrapWithFade(revSeq,  event.diedSprites.contains(event.defender)))

        // 8) Build matching wiggle sequences for stats (no color, but same timing)
        val statSeq = Actions.sequence(
            Actions.moveBy(moveDist, 0f, moveDur, Interpolation.sine),
            Actions.delay(flashDur * 2),
            Actions.moveBy(-moveDist, 0f, moveDur, Interpolation.sine)
        )
        val statRevSeq = Actions.sequence(
            Actions.moveBy(-moveDist, 0f, moveDur, Interpolation.sine),
            Actions.delay(flashDur * 2),
            Actions.moveBy(moveDist, 0f, moveDur, Interpolation.sine)
        )
        fun wrapStat(seq: Action, died: Boolean) =
            if (died) Actions.sequence(seq, Actions.fadeOut(fadeDur)) else seq

        // 9) Apply actions to stat tables
        attackerStats?.addAction(wrapStat(statSeq, event.diedSprites.contains(event.attacker)))
        defenderStats?.addAction(wrapStat(statRevSeq, event.diedSprites.contains(event.defender)))

        // 10) Schedule post-animation updates
        val totalTime = moveDur * 2 + flashDur * 2 + fadeDur
        stage.addAction(
            Actions.sequence(
                // wait for wiggle + fade to finish
                Actions.delay(totalTime),

                // then:
                Actions.run {
                    // a) Remove dead pet images + their stats tables
                    event.diedSprites.forEach { dead ->
                        findUIActorFor(dead)?.remove()
                        statTableMap.remove(dead)?.remove()
                    }

                    // b) Slide survivors (images + stats) into new slots
                    val slideTime = 0.5f

                    // Left team → slots 3,2,1,0
                    battleController.battle.playerA.team.teams
                        .filterIsInstance<Sprite>()
                        .take(4)
                        .forEachIndexed { i, sprite ->
                            val slot = 3 - i
                            val (x, y) = cellPositions[slot]

                            // image
                            findUIActorFor(sprite)
                                ?.addAction(Actions.moveTo(x, y, slideTime, Interpolation.sine))

                            // stats table
                            statTableMap[sprite]?.let { tbl ->
                                val statX = x + (200f - tbl.width) / 2f
                                val statY = y - tbl.height - 8f
                                tbl.addAction(Actions.moveTo(statX, statY, slideTime, Interpolation.sine))
                            }
                        }

                    // Right team → slots 5,6,7,8
                    battleController.battle.playerB.team.teams
                        .filterIsInstance<Sprite>()
                        .take(4)
                        .forEachIndexed { i, sprite ->
                            val slot = 5 + i
                            val (x, y) = cellPositions[slot]

                            findUIActorFor(sprite)
                                ?.addAction(Actions.moveTo(x, y, slideTime, Interpolation.sine))

                            statTableMap[sprite]?.let { tbl ->
                                val statX = x + (200f - tbl.width) / 2f
                                val statY = y - tbl.height - 8f
                                tbl.addAction(Actions.moveTo(statX, statY, slideTime, Interpolation.sine))
                            }
                        }

                    // c) Re-enable button if battle still ongoing
                    val leftAlive  = battleController.battle.playerA.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }
                    val rightAlive = battleController.battle.playerB.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }
                    nextAttackButton.isDisabled = !(leftAlive && rightAlive)
                    if (!leftAlive || !rightAlive) {
                        showBattleResult()
                    }

                },

                // wait for the slide to complete
                Actions.delay(0.5f),

                // finally, update numbers on all remaining stats tables
                Actions.run { updateStats() }
            )
        )
    }


    private fun updateStats() {
        statTableMap.forEach { (sprite, table) ->
            // children: 0=image(♥), 1=hpLabel, 2=image(⚔), 3=atkLabel
            (table.children[1] as? Label)?.setText(sprite.health.toString())
            (table.children[3] as? Label)?.setText(sprite.attack.toString())
        }
    }


    private fun showBattleResult() {
        // figure out who's left
        val leftAlive = battleController.battle.playerA.team.teams
            .filterIsInstance<Sprite>().any { it.health > 0 }
        val rightAlive = battleController.battle.playerB.team.teams
            .filterIsInstance<Sprite>().any { it.health > 0 }

        // decide what to say
        val resultText = when {
            leftAlive && !rightAlive  -> "You Win!"
            rightAlive && !leftAlive  -> "You Lose!"
            else                      -> "Draw!"
        }

        // disable further attacks
        nextAttackButton.isDisabled = true

        // build an overlay table, full‑screen
        val overlay = Table(skin).apply {
            setFillParent(true)
            top()
        }

        // large label
        val resultLabel = Label(resultText, skin).apply {
            setFontScale(4f)
        }

        // back‑to‑menu button
        val menuBtn = TextButton("Back to Menu", skin).apply {
            label.setFontScale(2f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MainMenuScreen(game)
                }
            })
        }

        // layout it: label, then a bit of space, then button
        overlay.add(resultLabel).center().padTop(200f)
        overlay.row()
        overlay.add(menuBtn).center().padTop(50f)

        stage.addActor(overlay)
    }


    /**
     * Finds the UI actor associated with a given sprite.
     */
    private fun findUIActorFor(sprite: Sprite): Image? {
        return battleFieldActors.find { it?.userObject == sprite } ?: null
    }

    /**
     * A minimal test scenario: each team gets up to four sprites.
     * This is used only if no player is provided from EditScreen.
     */
    private fun createBattleFromTeams(): BattleController {
        return BattleController(highscoreService = game.highscoreService).apply {
            battle.playerA.team.teams.addAll(teamA)
            if (gameMode == GameMode.SINGLEPLAYER && teamB.isEmpty()) {
                battle.playerB.team.teams.addAll(generateRandomTeam())
            } else {
                battle.playerB.team.teams.addAll(teamB)
            }
        }
    }



    private fun generateRandomTeam(): List<Sprite> {
        val options = listOf("cat", "dog", "bird")
        return List(4) {
            val name = options.random()
            when (name) {
                "cat"  -> Sprite().apply { this.name = "cat";  health = 10; attack = 2 }
                "dog"  -> Sprite().apply { this.name = "dog";  health = 5;  attack = 2 }
                "bird" -> Sprite().apply { this.name = "bird"; health = 5;  attack = 2 }
                else   -> Sprite().apply { this.name = "???";  health = 1;  attack = 1 }
            }
        }
    }


}
