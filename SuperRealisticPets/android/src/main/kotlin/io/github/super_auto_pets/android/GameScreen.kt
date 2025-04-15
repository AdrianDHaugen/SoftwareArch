package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import io.github.super_auto_pets.controller.BattleController
import io.github.super_auto_pets.controller.AttackEvent
import io.github.super_auto_pets.models.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Label

class GameScreen(private val game: Main) : Screen {

    private lateinit var stage: Stage
    private lateinit var uiSkin: Skin

    private lateinit var battleController: BattleController

    private val leftTeamTables = mutableListOf<Table>()
    private val rightTeamTables = mutableListOf<Table>()

    private lateinit var leftTeamTable: Table
    private lateinit var rightTeamTable: Table

    // We'll keep the list of attack events from startBattle
    private lateinit var attackEvents: List<AttackEvent>
    // We'll track which event we're on
    private var currentEventIndex = 0

    override fun show() {
        stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
        uiSkin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage

        // --- Background ---
        val bgTexture = Texture(Gdx.files.internal("battle_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // Build the battle scenario
        battleController = createTestBattle()

        // Build root table for layout
        val rootTable = Table()
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        leftTeamTable = Table(uiSkin)
        rightTeamTable = Table(uiSkin)
        rootTable.add(leftTeamTable).expand().left()
        rootTable.add(rightTeamTable).expand().right()

        // Populate initial UI (both teams)
        refreshTeamUI()

        // --- 1) Run the entire battle logic right now ---
        attackEvents = battleController.startBattle()

        // --- 2) Replay the events with animation ---
        if (attackEvents.isNotEmpty()) {
            replayNextEvent()
        } else {
            // Possibly no events if a team was empty from start
            println("No attacks happened. Possibly a draw or single-team scenario.")
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    /**
     * Recursively animate the next event in the list.
     */
    private fun replayNextEvent() {
        if (currentEventIndex >= attackEvents.size) {
            println("All events replayed. The battle is over.")
            return
        }

        val event = attackEvents[currentEventIndex]
        currentEventIndex++

        // Find the Scene2D images for attacker & defender
        val attackerImage = findUIActorFor(event.attacker, leftTeamTables, rightTeamTables)
        val defenderImage = findUIActorFor(event.defender, leftTeamTables, rightTeamTables)

        // Do a forward-lunge, flash the defender, move back, handle deaths, then go to next event
        attackerImage?.addAction(
            Actions.sequence(
                Actions.moveBy(100f, 0f, 0.25f), // attacker runs forward
                Actions.run {
                    // defender flash damage
                    defenderImage?.addAction(
                        Actions.sequence(
                            Actions.color(Color.RED, 0.1f),
                            Actions.color(Color.WHITE, 0.1f)
                        )
                    )
                },
                Actions.moveBy(-100f, 0f, 0.25f), // attacker moves back
                Actions.run {
                    // Handle any dead sprites from this event
                    for (deadSprite in event.diedSprites) {
                        doDefeatAnimation(deadSprite)
                    }
                    // Also, if we wanted to reflect new HP changes visually, we'd do that here
                    // e.g. updating a label or a health bar.  (Not shown in this snippet.)

                    // Then proceed to the next event after a small delay
                },
                Actions.delay(0.4f),
                Actions.run {
                    replayNextEvent()
                }
            )
        )
    }

    /**
     * Quick fade out for a defeated sprite. Remove from UI once done.
     */
    private fun doDefeatAnimation(sprite: Sprite) {
        val image = findUIActorFor(sprite, leftTeamTables, rightTeamTables) ?: return

        image.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.5f),
                    Actions.scaleTo(0f, 0f, 0.5f)
                ),
                Actions.run {
                    // remove from stage
                    image.remove()
                    leftTeamTables.remove(image)
                    rightTeamTables.remove(image)
                }
            )
        )
    }

    private fun refreshTeamUI() {
        leftTeamTable.clearChildren()
        rightTeamTable.clearChildren()

        // If you had: private val leftTeamImages = mutableListOf<Image>()
        // now, consider changing it to: private val leftTeamActors = mutableListOf<Actor>()
        // or private val leftTeamTables = mutableListOf<Table>()
        leftTeamTables.clear()

        // Rebuild the left side
        val leftSprites = battleController.battle.playerA.team.teams.filterIsInstance<Sprite>()
        for (sprite in leftSprites) {
            val petTable = createPetTable(sprite)
            leftTeamTables.add(petTable)  // store the Table instead of an Image
            leftTeamTable.add(petTable).pad(10f)
            //leftTeamTable.row() // if you want each in its own row; remove if side-by-side
        }

        // Same for right side
        rightTeamTables.clear()

        val rightSprites = battleController.battle.playerB.team.teams.filterIsInstance<Sprite>()
        for (sprite in rightSprites) {
            val petTable = createPetTable(sprite)
            rightTeamTables.add(petTable)
            rightTeamTable.add(petTable).pad(10f)
            //rightTeamTable.row()
        }
    }


    /**
     * Builds a small table containing:
     *  - The pet's image
     *  - A label with "HP: X, ATK: Y"
     */
    private fun createPetTable(sprite: Sprite): Table {
        val table = Table(uiSkin)

        // Load or pick the texture for the pet
        val textureFile = when (sprite.name) {
            "cat" -> "cat-1-base-nb.PNG"
            "dog" -> "dog-1-base-nb.PNG"
            else  -> "bird-1-base-nb.PNG"
        }

        val petTexture = Texture(Gdx.files.internal(textureFile))
        val petImage = Image(TextureRegionDrawable(TextureRegion(petTexture)))
        petImage.setSize(150f, 150f)

        // Load icon textures
        val heartTexture = Texture(Gdx.files.internal("heart.png"))
        val swordTexture = Texture(Gdx.files.internal("crossed_swords.png"))

        val heartImage = Image(TextureRegionDrawable(TextureRegion(heartTexture)))
        val swordImage = Image(TextureRegionDrawable(TextureRegion(swordTexture)))

        heartImage.setSize(50f, 50f)
        swordImage.setSize(50f, 50f)

        // Create labels for HP and Attack
        val hpLabel = Label("${sprite.health}", uiSkin)
        val atkLabel = Label("${sprite.attack}", uiSkin)

        // Create a horizontal row for stats
        val statsRow = Table(uiSkin)
        statsRow.add(swordImage).size(50f).padRight(5f)
        statsRow.add(atkLabel).padRight(15f)
        statsRow.add(heartImage).size(50f).padRight(5f)
        statsRow.add(hpLabel)

        // Lay them out
        table.add(petImage).size(200f, 200f)
        table.row()
        table.add(statsRow).padTop(5f)

        table.userObject = sprite
        return table
    }


    private fun findUIActorFor(sprite: Sprite, leftUI: List<Table>, rightUI: List<Table>): Table? {
        return leftUI.find { it.userObject == sprite } ?: rightUI.find { it.userObject == sprite }
    }

    /**
     * A minimal test scenario with 2 pets on each side
     */
    private fun createTestBattle(): BattleController {
        val bc = BattleController()

        val catA = Sprite().apply {
            name = "cat"
            health = 4
            attack = 2
        }
        val dogA = Sprite().apply {
            name = "dog"
            health = 5
            attack = 2
        }
        bc.battle.playerA.team.teams.add(catA)
        bc.battle.playerA.team.teams.add(dogA)

        val catB = Sprite().apply {
            name = "cat"
            health = 3
            attack = 3
        }
        val dogB = Sprite().apply {
            name = "dog"
            health = 100
            attack = 100
        }
        bc.battle.playerB.team.teams.add(catB)
        bc.battle.playerB.team.teams.add(dogB)

        return bc
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    override fun hide() {
        stage.dispose()
    }
    override fun pause() {}
    override fun resume() {}
    override fun dispose() {
        stage.dispose()
    }
}


