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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
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
    data class DragPayload(
        val drawable: Drawable,
        val sourceBox: Table
    )

    // Textures
    private val statBackground = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("statbackground.png"))))
    private val heartIcon = Image(TextureRegion(Texture(Gdx.files.internal("heart.png"))))
    private val currencyIcon = Image(TextureRegion(Texture(Gdx.files.internal("coin.png"))))
    private val hourglassIcon = Image(TextureRegion(Texture(Gdx.files.internal("hourglass.png"))))
    private val trophyIcon = Image(TextureRegion(Texture(Gdx.files.internal("trophy.png"))))

    // Load a sample texture
    val birdDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("bird-1-base-nb.PNG")))
    val catDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("cat-1-base-nb.PNG")))
    val dogDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("dog-1-base-nb.PNG")))
    val emptyDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("empty.png")))

    val animalTextures = listOf(birdDrawable, catDrawable,dogDrawable)

    // Create a target that allows unit boxes to receive dragged units
    private fun createUnitBoxTarget(box: Table, boxSize: Float, emptyDrawable: TextureRegionDrawable, skin: Skin): DragAndDrop.Target {
        return object : DragAndDrop.Target(box) {
            override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                // Visual feedback during drag
                box.background = skin.getDrawable("default-select")
                return true
            }

            override fun drop(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int) {
                // Reset background
                box.background = skin.getDrawable("default-window")

                // Get payload data
                val dragPayload = payload?.`object` as? DragPayload
                if (dragPayload != null) {
                    // Handle two cases: dragging from shop or swapping between unit boxes
                    val sourceBox = dragPayload.sourceBox

                    // If source is from unit table (switching positions)
                    if (sourceBox.getUserObject() == "unitBox") {
                        // Get the current content of target box
                        val targetContent = if (box.children.size > 0 && box.children.first() is Image) {
                            val targetImage = box.children.first() as Image
                            targetImage.drawable as? TextureRegionDrawable
                        } else {
                            null
                        }

                        // Clear both boxes
                        box.clearChildren()
                        sourceBox.clearChildren()

                        // Add dragged content to target box
                        val newImage = Image(dragPayload.drawable)
                        newImage.setScaling(Scaling.fit)
                        box.add(newImage).width(boxSize - 20f).height(boxSize - 20f)

                        // If target had content, move it to source box
                        if (targetContent != null) {
                            val swappedImage = Image(targetContent)
                            swappedImage.setScaling(Scaling.fit)
                            sourceBox.add(swappedImage).width(boxSize - 20f).height(boxSize - 20f)
                        } else {
                            // Otherwise add empty placeholder to source
                            val placeholder = Container<Image>(Image(emptyDrawable))
                            sourceBox.add(placeholder).width(boxSize - 20f).height(boxSize - 20f)
                        }

                        // Re-register drag sources for both boxes
                        setupDragSourceForUnitBox(box, boxSize)
                        setupDragSourceForUnitBox(sourceBox, boxSize)
                    }
                    // If source is from shop unit table
                    else {
                        // Clear target box
                        box.clearChildren()

                        // Add dragged content
                        val newImage = Image(dragPayload.drawable)
                        newImage.setScaling(Scaling.fit)
                        box.add(newImage).width(boxSize - 20f).height(boxSize - 20f)

                        // Clear source box (shop box) and add placeholder
                        sourceBox.clearChildren()
                        val placeholder = Container<Image>(Image(emptyDrawable))
                        sourceBox.add(placeholder).width(boxSize - 20f).height(boxSize - 20f)

                        // Re-register drag source for the unit box
                        setupDragSourceForUnitBox(box, boxSize)
                    }
                }
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                box.background = skin.getDrawable("default-window")
            }
        }
    }

    // Set up a drag source for a single unit box
    private fun setupDragSourceForUnitBox(box: Table, boxSize: Float) {
        // Only add drag source if the box has an image and it's not empty
        if (box.children.size > 0 && box.children.first() is Image) {
            val image = box.children.first() as Image
            if (image.drawable != null && image.drawable != emptyDrawable) {
                // Remove any existing drag sources for this actor
                dragAndDrop.removeSource(dragAndDrop.dragSource)

                // Add new drag source
                dragAndDrop.addSource(object : DragAndDrop.Source(image) {
                    override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                        val payload = DragAndDrop.Payload()
                        val dragActor = Image(image.drawable)
                        dragActor.setSize(boxSize, boxSize)
                        payload.dragActor = dragActor

                        // Store info about source and its content
                        payload.`object` = DragPayload(
                            image.drawable as TextureRegionDrawable,
                            box
                        )

                        // Visual feedback
                        image.color.a = 0.5f
                        return payload
                    }

                    override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                        // Reset opacity
                        image.color.a = 1f
                    }
                })
            }
        }
    }

    // Add drag sources to all unit boxes
    private fun setupDragSourcesForUnitBoxes(unitTable: Table, boxSize: Float) {
        for (box in unitTable.children) {
            if (box is Table && box.getUserObject() == "unitBox") {
                setupDragSourceForUnitBox(box, boxSize)
            }
        }
    }

    private fun rerollShop(shopTable: Table, boxSize: Float) {
        shopTable.clearChildren()

        repeat(4) { // or however many shop slots you want
            val shopBox = Table()
            shopBox.background = skin.getDrawable("default-window")

            val animalDrawable = animalTextures.random()
            val image = Image(animalDrawable)
            image.setScaling(Scaling.fit)

            shopBox.add(image).width(boxSize - 20f).height(boxSize - 20f)
            shopTable.add(shopBox).width(boxSize).height(boxSize).padRight(20f)

            // Register drag source for shop box
            dragAndDrop.addSource(object : DragAndDrop.Source(image) {
                override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = DragAndDrop.Payload()
                    val dragActor = Image(image.drawable)
                    dragActor.setSize(boxSize, boxSize)
                    payload.dragActor = dragActor
                    payload.`object` = DragPayload(animalDrawable, shopBox)
                    image.color.a = 0.5f
                    return payload
                }

                override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                    image.color.a = 1f
                }
            })
        }
    }


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
        container.top().left().pad(20f)

        // === Stats Table (root) ===
        val statsTable = Table()
        statsTable.setFillParent(true)
        statsTable.top().left()
        statsTable.add(container).top().left()

        // === Button Table ===
        val buttonTable = Table()
        buttonTable.bottom().right()
        buttonTable.setFillParent(true)

        // === Unit Table ===
        val unitTable = Table()
        unitTable.setPosition(850f, 490f)

        // Create 4 unit boxes
        for (i in 1..4) {
            val box = Table()
            box.background = skin.getDrawable("default-window")

            // Tag the box as belonging to the unit table
            box.setUserObject("unitBox")

            // Add a placeholder image
            val placeholder = Container<Image>(Image(emptyDrawable))
            placeholder.background = skin.getDrawable("default-pane-noborder")
            box.add(placeholder).width(boxSize - 20f).height(boxSize - 20f)

            // Add box to the row
            unitTable.add(box).width(boxSize).height(boxSize).padRight(20f)

            // Create a target for this box
            dragAndDrop.addTarget(createUnitBoxTarget(box, boxSize, emptyDrawable, skin))
        }
        // Set up drag sources initially
        setupDragSourcesForUnitBoxes(unitTable, boxSize)

        // Place unitTable in a container for better control
        val itemContainer = Container(unitTable)
        itemContainer.setSize(320f, 320f)
        itemContainer.setPosition(50f, 120f)
        itemContainer.background = skin.getDrawable("default-window")

        // Create table for items
        val itemTable = Table()
        itemTable.setPosition(1250f, 240f)
        itemTable.pad(10f)

        // Create item boxes
        for (i in 1..2) {
            val box = Table()
            box.background = skin.getDrawable("default-window")

            // Create an image using the unit sprite
            val unitImage = Image(birdDrawable)
            unitImage.setScaling(Scaling.fit)
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            itemTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)
        }

        // Place shopUnitTable in a container for better control
        val shopUnitContainer = Container(unitTable)
        shopUnitContainer.setSize(320f, 320f)
        shopUnitContainer.setPosition(50f, 200f)
        shopUnitContainer.background = skin.getDrawable("default-window")
        shopUnitContainer.pad(20f)

        // Shop Unit Table
        val shopUnitTable = Table()
        shopUnitTable.setPosition(630f, 240f)

        // Create shop unit boxes
        for (i in 1..4) {
            val box = Table()
            box.background = skin.getDrawable("default-window")

            // Create an image using the unit sprite
            val unitImage = Image(if (i % 2 == 0) birdDrawable else catDrawable)
            unitImage.setScaling(Scaling.fit)
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            shopUnitTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)

            // Add drag source for shop units
            dragAndDrop.addSource(object : DragAndDrop.Source(unitImage) {
                override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = DragAndDrop.Payload()
                    val dragActor = Image(unitImage.drawable)
                    dragActor.setSize(boxSize, boxSize)
                    payload.dragActor = dragActor

                    // Create our custom payload with source information
                    payload.`object` = DragPayload(unitImage.drawable, box)

                    // Visual feedback during drag
                    unitImage.color.a = 0.5f
                    return payload
                }

                override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                    // Reset opacity after drag
                    unitImage.color.a = 1f
                }
            })
        }

        val rerollTable = Table()
        rerollTable.setPosition(150f,230f)

        // Reroll button
        val rerollBtn = TextButton("reroll", skin)
        rerollBtn.label.setFontScale(2f)
        rerollBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                rerollShop(shopUnitTable,boxSize)
            }
        })
        rerollTable.add(rerollBtn).width(boxSize).height(boxSize).pad(30f)

        // Create button for starting the battle
        val startBattleBtn = TextButton("Start Battle", skin)
        startBattleBtn.label.setFontScale(4f)
        startBattleBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
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
        stage.addActor(rerollTable)
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
        // TODO: Implement pause functionality
    }

    override fun resume() {
        // TODO: Implement resume functionality
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
