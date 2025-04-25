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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.models.Sprite
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import io.github.super_auto_pets.controller.GameMode
import io.github.super_auto_pets.firebase.HighscoreManager
import io.github.super_auto_pets.managers.PlayerManager





class GameScreen(
    private val game: Main,
    private val gameMode: GameMode,
    private val teamA: List<Sprite> = emptyList(),
    private val teamB: List<Sprite> = emptyList()
) : Screen {


    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
        private const val AUTO_ATTACK_DELAY = 1.5f  // Time between auto attacks in seconds
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    private val heartTexture = Texture(Gdx.files.internal("stats/heart.png"))
    private val swordTexture = Texture(Gdx.files.internal("stats/crossed_swords.png"))
    private val startTexture = Texture(Gdx.files.internal("buttons/start.png"))
    private val backTexture  = Texture(Gdx.files.internal("backgrounds/back.png"))

    private val statTableMap = mutableMapOf<Sprite, Table>()
    private lateinit var battleController: BattleController

    private val cellPositions = mutableListOf<Pair<Float, Float>>()

    // Nine-slot array for the battle field.
    private val battleFieldActors: MutableList<Image?> = MutableList(9) { null }
    private lateinit var battleFieldTable: Table

    private var isAutoPlayMode = true
    private lateinit var nextAttackButton: ImageButton
    private lateinit var autoPlayButton: ImageButton
    private lateinit var manualPlayButton: ImageButton


    // Auto battle variables
    private var battleStarted = false
    private var timeSinceLastAttack = 0f
    private var battleInProgress = true
    private var waitingForAnimation = false

    // Start- and abort-battle buttons
    private lateinit var startBattleButton: ImageButton
    private lateinit var abortBattleButton: ImageButton
    private lateinit var buttonTable: Table


    //Pause variables
    private val pauseTexture = Texture(Gdx.files.internal("buttons/pause.png"))
    private val resumeTexture = Texture(Gdx.files.internal("buttons/resume.png"))
    private lateinit var pauseButton: ImageButton
    private lateinit var resumeButton: ImageButton
    private var isPaused = false
    private var buttonSafePadding = 0f

    override fun show() {
        Gdx.input.inputProcessor = stage

        //
        // 1) CREATE & ASSIGN ALL BUTTONS
        //

        // Auto-Play
        autoPlayButton = ImageButton(
            TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("buttons/auto_play.png"))))
        ).apply {
            imageCell.size(200f, 200f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    isAutoPlayMode = true
                    beginBattle()
                }
            })
        }

        // Step-by-Step
        manualPlayButton = ImageButton(
            TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("buttons/step_by_step.png"))))
        ).apply {
            imageCell.size(200f, 200f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    isAutoPlayMode = false
                    beginBattle()
                }
            })
        }

        // Next Attack (manual only)
        nextAttackButton = ImageButton(
            TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("buttons/next_attack.png"))))
        ).apply {
            imageCell.size(200f, 200f)
            isVisible = false
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    if (battleInProgress && !waitingForAnimation && !isPaused) {
                        performBattleStep()
                    }
                }
            })
        }

        // Pause
        pauseButton = ImageButton(TextureRegionDrawable(TextureRegion(pauseTexture))).apply {
            imageCell.size(150f, 150f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    isPaused = true
                    pauseButton.remove()
                    buttonTable.clear()
                    buttonTable.padBottom(50f)
                    buttonTable.add(resumeButton).pad(20f)
                }
            })
        }

        // Resume
        resumeButton = ImageButton(TextureRegionDrawable(TextureRegion(resumeTexture))).apply {
            imageCell.size(150f, 150f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    isPaused = false
                    resumeButton.remove()
                    buttonTable.clear()
                    buttonTable.padBottom(50f)
                    if (isAutoPlayMode) {
                        buttonTable.add(pauseButton).pad(20f)
                    } else {
                        buttonTable.add(nextAttackButton).pad(20f)
                    }
                }
            })
        }

        //
        // 2) SET UP BATTLEFIELD
        //

        // compute 9 cell positions
        val cellW = viewport.worldWidth  / 9f
        val cellY = viewport.worldHeight / 2f - 75f
        cellPositions.clear()
        for (i in 0 until 9) {
            cellPositions.add(i * cellW + (cellW - 150f) / 2 to cellY)
        }

        // background
        val bgTex = Texture(Gdx.files.internal("backgrounds/battle_bg.png"))
        val bg    = Image(TextureRegionDrawable(TextureRegion(bgTex))).apply {
            setSize(viewport.worldWidth, viewport.worldHeight)
        }
        stage.addActor(bg)

        // battle model + UI
        battleController = createBattleFromTeams()
        battleFieldTable = Table(skin).apply { setFillParent(true) }
        stage.addActor(battleFieldTable)
        repeat(9) {
            battleFieldTable.add().width(cellW).height(220f).pad(10f)
        }
        battleFieldTable.row()
        refreshBattleFieldUI()

        //
        // 3) BUILD YOUR BUTTON TABLE (one single table for ALL modes)
        //

        // after computing cellW and cellY:
        val buttonHeight = 200f   // the size you give your ImageButtons
        // +300f because that matched your trial — adjust to taste
        buttonSafePadding = (cellY - buttonHeight + 300f).coerceAtLeast(0f)

        buttonTable = Table(skin).apply {
            setFillParent(true)
            bottom().center()
            padBottom(buttonSafePadding)
            add(autoPlayButton).pad(20f)
            add(manualPlayButton).pad(20f)
        }
        stage.addActor(buttonTable)



        //
        // 4) BACK/ABORT BUTTON
        //

        val abortDrawable = TextureRegionDrawable(TextureRegion(backTexture))
        abortBattleButton = ImageButton(abortDrawable).apply {
            imageCell.size(150f, 150f)
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    game.screen = MainMenuScreen(game)
                }
            })
        }
        val abortTable = Table(skin).apply {
            setFillParent(true)
            top().left()
            add(abortBattleButton).pad(20f)
        }
        stage.addActor(abortTable)
    }



    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Only auto-advance when in auto-play mode
        if (battleStarted && battleInProgress && !waitingForAnimation && !isPaused && isAutoPlayMode) {
            timeSinceLastAttack += delta
            if (timeSinceLastAttack >= AUTO_ATTACK_DELAY) {
                timeSinceLastAttack = 0f
                performBattleStep()
            }
        }

        stage.act(delta)
        stage.draw()
    }


    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    override fun hide() {
        stage.dispose()
        skin.dispose()
    }
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        stage.dispose()
        skin.dispose()
        pauseTexture.dispose()
        resumeTexture.dispose()
    }

    private fun beginBattle() {
        battleStarted = true

        autoPlayButton.remove()
        manualPlayButton.remove()

        buttonTable.clear()
        buttonTable.padBottom(buttonSafePadding)

        if (isAutoPlayMode) {
            buttonTable.add(pauseButton).pad(20f)
        }
        if (!isAutoPlayMode) {
            nextAttackButton.isVisible = true
            buttonTable.add(nextAttackButton).pad(20f)
        }
    }





    private fun refreshBattleFieldUI() {
        // 1) remove old pet Images…
        battleFieldActors.forEach { it?.remove() }
        battleFieldActors.clear()

        // 2) …and also toss any old stat‐tables
        statTableMap.values.forEach { it.remove() }
        statTableMap.clear()

        // 3) pull from your controller exactly the same way…
        val teamLeft = battleController.battle.playerA.team.teams.filterIsInstance<Sprite>()
        val teamRight = battleController.battle.playerB.team.teams.filterIsInstance<Sprite>()

        // 4) build a 9‑slot list of Sprites (or null)
        val slots = MutableList<Sprite?>(9) { null }
        teamLeft.take(4).forEachIndexed { i, s -> slots[3 - i] = s }
        teamRight.take(4).forEachIndexed { i, s -> slots[5 + i] = s }

        // 5) now place each Sprite's Image (and stats table) at the precalculated x,y
        slots.forEachIndexed { idx, sprite ->
            if (sprite != null) {
                // — Image
                val img = createPetImage(sprite)
                if (idx >= 5) {
                    // set the origin to the center of the image
                    img.setOrigin(img.width/2f, img.height/2f)
                    // flip horizontally
                    img.setScaleX(-1f)
                }
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

    // --- all sprite paths from sprites.json, keyed by "name-color" -------------
    private val spritePathMap: Map<String, String> by lazy {
        val jsonText   = Gdx.files.internal("units/sprites.json").readString()
        val root       = com.badlogic.gdx.utils.JsonReader().parse(jsonText)
        val map        = mutableMapOf<String, String>()

        for (i in 0 until root.size) {
            val obj   = root.get(i)
            val key   = obj.getString("name") + "-" + obj.getString("color", "base")
            val path  = obj.getString("path")
            map[key]  = path
        }
        map
    }


    private fun createPetImage(sprite: Sprite): Image {
        val colour  = sprite.color?.takeIf { it.isNotBlank() } ?: "base"
        val key     = "${sprite.name}-$colour"
        val file    = spritePathMap[key]                       // exact match
            ?: spritePathMap["${sprite.name}-base"]     // fallback
            ?: "stats/heart.png"                              // final safety net

        val tex = texCache.getOrPut(file) { Texture(Gdx.files.internal(file)) }
        return Image(TextureRegionDrawable(TextureRegion(tex))).apply {
            setSize(200f, 200f)
            userObject = sprite
        }
    }


    /**
     * Processes one battle step.
     */
    private fun performBattleStep() {
        // Set flag to prevent starting another animation while one is in progress
        waitingForAnimation = true

        // Process one battle step
        val event = battleController.nextAttackStep()
        if (event == null) {
            println("Battle is over!")
            battleInProgress = false
            showBattleResult()
            return
        }

        // Find UI actors for attacker/defender (images + stats tables)
        val attackerActor = findUIActorFor(event.attacker)
        val defenderActor = findUIActorFor(event.defender)
        val attackerStats = statTableMap[event.attacker]
        val defenderStats = statTableMap[event.defender]

        // Timing parameters
        val moveDist = 100f
        val moveDur  = 0.3f
        val flashDur = 0.2f
        val fadeDur  = 0.5f

        // Build wiggle sequences for images (with color flash)
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

        // Helper to append fade-out if the sprite died this round
        fun wrapWithFade(seq: Action, died: Boolean) =
            if (died) Actions.sequence(seq, Actions.fadeOut(fadeDur)) else seq

        // Apply actions to pet images
        attackerActor?.addAction(wrapWithFade(baseSeq, event.diedSprites.contains(event.attacker)))
        defenderActor?.addAction(wrapWithFade(revSeq,  event.diedSprites.contains(event.defender)))

        // Build matching wiggle sequences for stats (no color, but same timing)
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

        // Apply actions to stat tables
        attackerStats?.addAction(wrapStat(statSeq, event.diedSprites.contains(event.attacker)))
        defenderStats?.addAction(wrapStat(statRevSeq, event.diedSprites.contains(event.defender)))

        // Schedule post-animation updates
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
                                tbl.addAction(
                                    Actions.moveTo(
                                        statX,
                                        statY,
                                        slideTime,
                                        Interpolation.sine
                                    )
                                )
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

                    // c) Check if battle is still ongoing
                    val leftAlive  = battleController.battle.playerA.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }
                    val rightAlive = battleController.battle.playerB.team.teams
                        .filterIsInstance<Sprite>().any { it.health > 0 }

                    if (!leftAlive || !rightAlive) {
                        battleInProgress = false
                        showBattleResult()
                    }
                },

                // wait for the slide to complete
                Actions.delay(0.5f),

                // finally, update numbers on all remaining stats tables
                Actions.run {
                    updateStats()
                    // Animation is complete, reset flag to allow next attack
                    waitingForAnimation = false
                }
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
        buttonTable.clear()

        val leftAlive = battleController.battle.playerA.team.teams
            .filterIsInstance<Sprite>().any { it.health > 0 }
        val rightAlive = battleController.battle.playerB.team.teams
            .filterIsInstance<Sprite>().any { it.health > 0 }

        // --- Decide who won ---
        val resultText: String = when {
            leftAlive && !rightAlive -> {
                // Player A wins
                HighscoreManager.appendWin(PlayerManager.playerAName)
                if (gameMode == GameMode.LOCAL_MULTIPLAYER) {
                    HighscoreManager.resetStreak(PlayerManager.playerBName)
                    "Winner: ${PlayerManager.playerAName}"

                } else {
                    "You Win!"
                }
            }
            rightAlive && !leftAlive -> {
                // Player B wins
                if (gameMode == GameMode.LOCAL_MULTIPLAYER && PlayerManager.playerBName.isNotBlank()) {
                    HighscoreManager.appendWin(PlayerManager.playerBName)
                    HighscoreManager.resetStreak(PlayerManager.playerAName)
                    "Winner: ${PlayerManager.playerBName}"
                } else {
                    "You Lose!"
                }
            }
            else -> {
                "Draw!"
            }
        }

        // --- Build the result overlay ---
        val overlay = Table(skin).apply {
            setFillParent(true)
            top()
        }

        val resultLabel = Label(resultText, skin).apply {
            setFontScale(4f)
        }

        overlay.add(resultLabel).center().padTop(200f)

        stage.addActor(overlay)
    }




    /**
     * Finds the UI actor associated with a given sprite.
     */
    private fun findUIActorFor(sprite: Sprite): Image? {
        return battleFieldActors.find { it?.userObject == sprite } ?: null
    }

    /**
     * This function creates battle between the teams made in editscreen.
     * If game == singleplayer it creates a randomteam with the helpfunctin
     * generateRandomTeam
     */
    private fun createBattleFromTeams(): BattleController {
        return BattleController(highscoreService = game.highscoreService).apply {
            battle.playerA.team.teams.addAll(teamA.asReversed())
            if (gameMode == GameMode.SINGLEPLAYER && teamB.isEmpty()) {
                battle.playerB.team.teams.addAll(generateRandomTeam())
            } else {
                battle.playerB.team.teams.addAll(teamB.asReversed())
            }
        }
    }
}

/**
 * Helperfunction for createBattleFromTeams in singleplayer-mode.
 */
private fun generateRandomTeam(): List<Sprite> {
    val options = listOf("cat", "dog", "bird", "fish")
    return List(4) {
        val name = options.random()
        when (name) {
            "cat"  -> Sprite().apply { this.name = "cat";  health = 2;  attack = 2 }
            "dog"  -> Sprite().apply { this.name = "dog";  health = 3;  attack = 2 }
            "bird" -> Sprite().apply { this.name = "bird"; health = 1;  attack = 2 }
            "fish" -> Sprite().apply { this.name = "fish"; health = 1;  attack = 1 }
            else   -> Sprite().apply { this.name = "???";  health = 1;  attack = 1 }
        }
    }
}
