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

class GameScreen(private val game: Main) : Screen {

    private lateinit var stage: Stage
    private lateinit var uiSkin: Skin

    private val leftTeam = mutableListOf<Image>()
    private val rightTeam = mutableListOf<Image>()

    // We'll keep references to the tables for rebuild logic
    private lateinit var leftTeamTable: Table
    private lateinit var rightTeamTable: Table

    override fun show() {
        stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
        uiSkin = Skin(Gdx.files.internal("uiskin.json"))
        Gdx.input.inputProcessor = stage

        // ---- 1) BACKGROUND ----
        val bgTexture = Texture(Gdx.files.internal("battle_bg.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // ---- 2) ROOT TABLE ----
        val rootTable = Table()
        rootTable.setFillParent(true)
        stage.addActor(rootTable)

        // ---- 3) CREATE TEAM TABLES ----
        leftTeamTable = Table(uiSkin)
        rightTeamTable = Table(uiSkin)

        // ---- 4) ADD SPRITES VIA HELPER FUNCTION ----
        addTeamSprites(
            team = leftTeam,
            teamTable = leftTeamTable,
            spritePath = "cat-1-base-nb.PNG",
            count = 5
        )
        addTeamSprites(
            team = rightTeam,
            teamTable = rightTeamTable,
            spritePath = "dog-1-base-nb.PNG",
            count = 5
        )

        // ---- 5) LAYOUT TEAMS IN ROOT TABLE ----
        rootTable.add(leftTeamTable).expand().left()
        rootTable.add(rightTeamTable).expand().right()

        // ---- 6) DEMO ACTIONS ----
        stage.addAction(
            Actions.sequence(
                Actions.delay(2f),
                Actions.run {
                    // Example: leftTeam[4] attacks rightTeam[0]
                    doAttackAnimation(leftTeam[4], rightTeam[0])
                },
                Actions.delay(1f),
                Actions.run {
                    // Example: flash damage on rightTeam[0]
                    doDamageFlash(rightTeam[0])
                },
                Actions.delay(1.5f),
                Actions.run {
                    // Example: "defeat" rightTeam[0]
                    doDefeatAnimation(rightTeam[0], rightTeam, rightTeamTable)
                }
            )
        )
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    // ---- HELPERS ----

    /**
     * Helper function to add a given number of sprites to a team and table.
     * This keeps code DRY for the left vs. right team creation.
     */
    private fun addTeamSprites(
        team: MutableList<Image>,
        teamTable: Table,
        spritePath: String,
        count: Int
    ) {
        for (i in 1..count) {
            val petTexture = Texture(Gdx.files.internal(spritePath))
            val region = TextureRegionDrawable(TextureRegion(petTexture))
            val pet = Image(region)

            // Calculate aspect ratio
            val originalWidth = petTexture.width.toFloat()
            val originalHeight = petTexture.height.toFloat()
            val aspectRatio = originalHeight / originalWidth

            // Fix width = 150, scale height accordingly
            val targetWidth = 150f
            val targetHeight = targetWidth * aspectRatio

            // Add to the team list
            team.add(pet)

            // Add to the table
            teamTable.add(pet).size(targetWidth, targetHeight).pad(20f)
        }
    }

    // Simple forward-and-back "lunge" at the target
    private fun doAttackAnimation(attacker: Image, target: Image) {
        attacker.addAction(
            Actions.sequence(
                Actions.moveBy(100f, 0f, 0.25f),
                Actions.moveBy(-100f, 0f, 0.25f)
            )
        )
    }

    // Quick color flash to show damage
    private fun doDamageFlash(pet: Image) {
        val originalColor = Color.WHITE
        pet.addAction(
            Actions.sequence(
                Actions.color(Color.RED, 0.1f),
                Actions.color(originalColor, 0.1f)
            )
        )
    }

    // Defeat animation: fade + shrink, then remove + shift
    private fun doDefeatAnimation(
        pet: Image,
        team: MutableList<Image>,
        teamTable: Table
    ) {
        pet.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.5f),
                    Actions.scaleTo(0f, 0f, 0.5f)
                ),
                Actions.run {
                    // 1) Remove from stage
                    pet.remove()

                    // 2) Remove from the team list
                    val index = team.indexOf(pet)
                    if (index != -1) {
                        team.removeAt(index)
                    }

                    // 3) Rebuild the table
                    refreshTeamTable(team, teamTable)
                }
            )
        )
    }

    /**
     * Clears the table and re-adds each pet in the updated list.
     * This ensures the next pet in line visually shifts up in the table.
     */
    private fun refreshTeamTable(team: List<Image>, teamTable: Table) {
        // Remove all children so we can re-add them in new order
        teamTable.clearChildren()

        // Re-add each pet
        for (pet in team) {
            teamTable.add(pet).width(150f).height(150f).pad(20f)
        }
    }

    // ---- Screen Lifecycle ----

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
