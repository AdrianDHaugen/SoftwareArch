package io.github.some_example_name.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.FitViewport

class EditScreen (private val game: Main) : Screen {
    private val stage = Stage(FitViewport(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    //Textures
    private val statBackground = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("statbackground.png"))))
    private val heartIcon = Image(TextureRegion(Texture(Gdx.files.internal("heart.png"))))
    private val currencyIcon = Image(TextureRegion(Texture(Gdx.files.internal("coin.png"))))
    private val hourglassIcon = Image(TextureRegion(Texture(Gdx.files.internal("hourglass.png"))))
    private val trophyIcon = Image(TextureRegion(Texture(Gdx.files.internal("trophy.png"))))

    override fun show() {

        val boxSize = 200f


        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage
        Gdx.app.log("DEBUG", "File exists? " + Gdx.files.internal("uiskin.json").exists())
        // Create a root table for layout
        // Main container
        val root = Table()
        root.setFillParent(true)
        stage.addActor(root)

// === Mini Table to hold heart + label ===
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
        statsTable.debug()

        statsTable.add(container).top().left()

// === Button Table ===
        val buttonTable = Table()
        buttonTable.debug()
        buttonTable.bottom().right()
        buttonTable.setFillParent(true)

// Load a sample texture
        val unitTexture = Texture(Gdx.files.internal("uiskin.png")) // or from an atlas
        val unitDrawable = TextureRegionDrawable(TextureRegion(unitTexture))

// === Unit Table ===
        val unitTable = Table()
        unitTable.debug()
        unitTable.setPosition(850f,600f)

// Create 4 unit boxes
        for (i in 1..5) {
            val box = Table()
            box.debug()
            box.background = skin.getDrawable("default-window") // Box style

            // Create an image using the unit sprite
            val unitImage = Image(unitDrawable)
            unitImage.setScaling(Scaling.fit) // Scale to fit inside the box
            box.add(unitImage).width(boxSize).height(boxSize).pad(10f)

            // Add box to the row
            unitTable.add(box).width(boxSize).height(boxSize).padRight(20f)
        }

// Place unitTable in a container for better control
        val boxContainer = Container(unitTable)
        boxContainer.setSize(320f, 320f)
        boxContainer.setPosition(50f, 100f) // position it wherever you want
        boxContainer.background = skin.getDrawable("default-window")

// Create table for items
        val itemTable = Table()
        itemTable.debug()
        itemTable.setPosition(1500f,200f)
        itemTable.pad(10f)

        // Create 4 unit boxes
        for (i in 1..2) {
            val box = Table()
            box.debug()
            box.background = skin.getDrawable("default-window") // Box style

            // Create an image using the unit sprite
            val unitImage = Image(unitDrawable)
            unitImage.setScaling(Scaling.fit) // Scale to fit inside the box
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            itemTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)
        }

// Place unitTable in a container for better control
        val itemContainer = Container(unitTable)
        itemContainer.setSize(320f, 320f)
        itemContainer.setPosition(50f, 200f) // position it wherever you want
        itemContainer.background = skin.getDrawable("default-window")
        itemContainer.pad(20f)

        val shopUnitTable = Table()
        shopUnitTable.debug()
        shopUnitTable.setPosition(600f,200f)

        // Create 4 unit boxes
        for (i in 1..4) {
            val box = Table()
            box.debug()
            box.background = skin.getDrawable("default-window") // Box style

            // Create an image using the unit sprite
            val unitImage = Image(unitDrawable)
            unitImage.setScaling(Scaling.fit) // Scale to fit inside the box
            box.add(unitImage).width(boxSize).height(boxSize).pad(20f)

            // Add box to the row
            shopUnitTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)
        }



// Create button for starting the battle
        val singlePlayerBtn = TextButton("Start Battle", skin)
        singlePlayerBtn.label.setFontScale(4f)
        singlePlayerBtn.addListener(object : ClickListener() {
            override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent?, x: Float, y: Float) {
                game.screen = MainMenuScreen(game)
            }
        })
        buttonTable.add(singlePlayerBtn).width(400f).height(400f).pad(30f)

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
