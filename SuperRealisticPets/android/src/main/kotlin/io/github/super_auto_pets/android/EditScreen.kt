package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.FitViewport

class EditScreen (private val game: Main) : Screen {
    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))
    private val dragAndDrop = DragAndDrop()

    //Textures
    private val statBackground = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("statbackground.png"))))
    private val heartIcon = Image(TextureRegion(Texture(Gdx.files.internal("heart.png"))))
    private val currencyIcon = Image(TextureRegion(Texture(Gdx.files.internal("coin.png"))))
    private val hourglassIcon = Image(TextureRegion(Texture(Gdx.files.internal("hourglass.png"))))
    private val trophyIcon = Image(TextureRegion(Texture(Gdx.files.internal("trophy.png"))))

    override fun show() {

        val boxSize = 150f


        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage
        Gdx.app.log("DEBUG", "File exists? " + Gdx.files.internal("uiskin.json").exists())

        // --- Background ---
        val bgTexture = Texture(Gdx.files.internal("editorbackground.png"))
        val bgImage = Image(TextureRegionDrawable(TextureRegion(bgTexture)))
        bgImage.setSize(stage.viewport.worldWidth, stage.viewport.worldHeight)
        stage.addActor(bgImage)

        // Create a root table for layout
        // Main container
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

// === Mini Table to hold stats ===
        val miniTable = Table()
        miniTable.left().top()

        val fontScale = 4f
        val spaceBetweenObjects = 15f
        val iconSize = 100f

        miniTable.add(currencyIcon).size(iconSize,iconSize).padLeft(spaceBetweenObjects)
        val currencyLabel = Label("10 ", skin, "default")
        currencyLabel.setFontScale(fontScale)
        currencyLabel.style.background = statBackground
        miniTable.add(currencyLabel).padLeft(spaceBetweenObjects)

        miniTable.add(heartIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        val heartLabel = Label("10 ", skin, "default")
        heartLabel.setFontScale(fontScale)
        heartLabel.style.background = statBackground
        miniTable.add(heartLabel).padLeft(spaceBetweenObjects)

        miniTable.add(hourglassIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        val hourglassLabel = Label("10 ", skin, "default")
        hourglassLabel.setFontScale(fontScale)
        hourglassLabel.style.background = statBackground
        miniTable.add(hourglassLabel).padLeft(spaceBetweenObjects)

        miniTable.add(trophyIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        val trophyLabel = Label("10 ", skin, "default")
        trophyLabel.setFontScale(fontScale)
        trophyLabel.style.background = statBackground
        miniTable.add(trophyLabel).padLeft(spaceBetweenObjects)







// === Container to wrap miniTable ===
        val container = Container(miniTable)
        container.top().left().pad(20f) // Add padding/margin if needed

// === Stats Table (root) ===
        val statsTable = Table()
        statsTable.setFillParent(true)
        statsTable.top().left()
        //statsTable.debug()

        statsTable.add(container).top().left()

// === Button Table ===
        val buttonTable = Table()
        //buttonTable.debug()
        buttonTable.bottom().right()
        buttonTable.setFillParent(true)

// Load a sample texture
        val birdDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("bird-1-base-nb.PNG"))) // or from an atlas
        val catDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("cat-1-base-nb.PNG")))
        val emptyDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("empty.png")))


// === Unit Table ===
        val unitTable = Table()
        unitTable.setPosition(850f, 490f)

// Create 4 unit boxes
        for (i in 1..4) {
            val box = Table()
            box.background = skin.getDrawable("default-window")

            // Important: Add a placeholder or empty container to make the drop area visible
            // This gives the box initial content that can be replaced
            val placeholder = Container<Image>(Image(emptyDrawable))
            placeholder.background = skin.getDrawable("default-pane-noborder")
            box.add(placeholder).width(boxSize - 20f).height(boxSize - 20f)

            // Add box to the row
            unitTable.add(box).width(boxSize).height(boxSize).padRight(20f)

            // Drag target setup - IMPORTANT: target must be the box itself
            dragAndDrop.addTarget(object : DragAndDrop.Target(box) {
                override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                    // Add visual feedback during drag-over
                    box.background = skin.getDrawable("default-select")
                    Gdx.app.log("DEBUG","drag")
                    return true
                }

                override fun drop(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int) {
                    // Reset background
                    box.background = skin.getDrawable("default-window")

                    // Clear existing content
                    box.clearChildren()

                    // Extract the drawable from the payload
                    val drawable = payload?.`object` as? TextureRegionDrawable
                    Gdx.app.log("DEBUG","TEST DROP")
                    if (drawable != null) {
                        val newImage = Image(drawable)
                        newImage.setScaling(Scaling.fit)
                        box.add(newImage).width(boxSize - 20f).height(boxSize - 20f)
                        Gdx.app.log("DEBUG","drawable = Null")
                    }
                }

                override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                    // Reset background if drag is canceled
                    box.background = skin.getDrawable("default-window")
                }
            })
        }

// Place unitTable in a container for better control
        val itemContainer = Container(unitTable)
        itemContainer.setSize(320f, 320f)
        itemContainer.setPosition(50f, 120f) // position it wherever you want
        itemContainer.background = skin.getDrawable("default-window")

// Create table for items
        val itemTable = Table()
        //itemTable.debug()
        itemTable.setPosition(1250f,240f)
        itemTable.pad(10f)

        // Create 4 unit boxes
        for (i in 1..2) {
            val box = Table()
            //box.debug()
            box.background = skin.getDrawable("default-window") // Box style

            // Create an image using the unit sprite
            val unitImage = Image(birdDrawable)
            unitImage.setScaling(Scaling.fit) // Scale to fit inside the box
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            itemTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)
        }

        // Place shopUnitTable in a container for better control
        val shopUnitContainer = Container(unitTable)
        shopUnitContainer.setSize(320f, 320f)
        shopUnitContainer.setPosition(50f, 200f) // position it wherever you want
        shopUnitContainer.background = skin.getDrawable("default-window")
        shopUnitContainer.pad(20f)

        val shopUnitTable = Table()
        //shopUnitTable.debug()
        shopUnitTable.setPosition(500f,240f)

        val rerollBtn = TextButton("reroll", skin)
        rerollBtn.label.setFontScale(2f)
        rerollBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                TODO("Not yet implemented")
            }
        })
        shopUnitTable.add(rerollBtn).width(boxSize).height(boxSize).pad(20f)

        // Create 4 unit boxes
        for (i in 1..4) {
            val box = Table()
            //box.debug()
            box.background = skin.getDrawable("default-window") // Box style

            // Create an image using the unit sprite
            val unitImage = Image(birdDrawable)
            unitImage.setScaling(Scaling.fit) // Scale to fit inside the box
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            shopUnitTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)

// In your shop unit boxes section:
            dragAndDrop.addSource(object : DragAndDrop.Source(unitImage) {
                override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = DragAndDrop.Payload()
                    val dragActor = Image(unitImage.drawable)
                    dragActor.setSize(boxSize, boxSize)
                    payload.dragActor = dragActor

                    // Make sure to properly set the object
                    payload.`object` = unitImage.drawable

                    // Optional: Add visual feedback that item is being dragged
                    unitImage.color.a = 0.5f
                    Gdx.app.log("DEBUG", "TEST")
                    return payload
                }

                override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                    // Reset opacity after drag
                    unitImage.color.a = 1f
                }
            })
        }


// Create button for starting the battle
        val startBattleBtn = TextButton("Start Battle", skin)
        startBattleBtn.label.setFontScale(4f)
        startBattleBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game)
            }
        })
        buttonTable.add(startBattleBtn).width(400f).height(400f).pad(30f)

// Add tables to the stage
        stage.addActor(statsTable)
        stage.addActor(buttonTable)
        stage.addActor(unitTable)
        stage.addActor(itemTable)
        stage.addActor(shopUnitTable)

    }

    override fun render(delta: Float) {
        // Update/draw stage
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
