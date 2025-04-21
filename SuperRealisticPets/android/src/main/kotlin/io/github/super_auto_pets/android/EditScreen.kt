package io.github.super_auto_pets.android

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
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
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import io.github.super_auto_pets.controller.PlayerController
import io.github.super_auto_pets.controller.ShopController
import io.github.super_auto_pets.interfaces.GameUnit
import io.github.super_auto_pets.models.Sprite
import io.github.super_auto_pets.models.Item
import io.github.super_auto_pets.models.Player
import io.github.super_auto_pets.models.Shop
import io.github.super_auto_pets.models.Team

class EditScreen (private val game: Main) : Screen {
    companion object {
        private const val VIRTUAL_WIDTH = 1920f
        private const val VIRTUAL_HEIGHT = 1080f
    }

    private val viewport = FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT)
    private val stage = Stage(viewport)
    private val skin = Skin(Gdx.files.internal("uiskin.json"))
    private val dragAndDrop = DragAndDrop()

    //player
    private lateinit var player:Player
    private lateinit var playerController: PlayerController

    //Shop
    private lateinit var shop:Shop
    private lateinit var shopController: ShopController

    data class DragPayload(
        val drawable: Drawable,
        val sourceBox: Table,
        val sprite: Sprite? = null
    )

    // Textures
    private val statBackground = TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("statbackground.png"))))
    private val spriteFrame = TextureRegionDrawable(Texture(Gdx.files.internal("spriteframe.png")))
    private val currencyIcon = Image(TextureRegion(Texture(Gdx.files.internal("coin.png"))))
    private val hourglassIcon = Image(TextureRegion(Texture(Gdx.files.internal("hourglass.png"))))
    private val trophyIcon = Image(TextureRegion(Texture(Gdx.files.internal("trophy.png"))))

    // Load textures
    private val birdDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("bird-1-base-nb.PNG")))
    private val emptyDrawable = TextureRegionDrawable(Texture(Gdx.files.internal("empty.png")))

    private val spriteTextures = mutableMapOf<String, TextureRegionDrawable>()

    //Create buttons
    private val texture = Texture(Gdx.files.internal("rerollbtn.png"))
    private val drawable = TextureRegionDrawable(TextureRegion(texture))
    private val rerollBtn = ImageButton(drawable)

    //Sizes
    val boxSize = 180f
    val spriteSize = 100f

    // Player stats
    private var playerHealth = 10
    private var playerTurn = 1
    private var playerTrophies = 0

    // References to UI elements
    private lateinit var currencyLabel: Label
    private lateinit var heartLabel: Label
    private lateinit var hourglassLabel: Label
    private lateinit var trophyLabel: Label


    private fun createUnitBoxTarget(
        box: Table,
        boxSize: Float,
        emptyDrawable: TextureRegionDrawable,
        skin: Skin
    ): DragAndDrop.Target {
        return object : DragAndDrop.Target(box) {
            override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                box.background = spriteFrame
                return true
            }

            override fun drop(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int) {
                box.background = spriteFrame

                val dragPayload = payload?.`object` as? DragPayload ?: return
                val sourceBox = dragPayload.sourceBox
                val draggedSprite = dragPayload.sprite
                val fromUnitBox = sourceBox.getUserObject("type") == "unitBox"

                if (fromUnitBox) {
                    // Moving a unit from one unitBox to another
                    if (draggedSprite != null) {
                        // 1. Add to target box
                        box.clearChildren()
                        sourceBox.clearChildren()
                        addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                        box.setUserObject("sprite", draggedSprite)
                        setupDragSourceForUnitBox(box, boxSize)

                        // 2. Clear source box and add empty placeholder
                        sourceBox.clearChildren()
                        val placeholder = Container(Image(emptyDrawable))
                        sourceBox.add(placeholder).width(spriteSize).height(spriteSize)
                        sourceBox.setUserObject("type","unitBox")
                    }

                } else {
                    // Buying from the shop
                    if (draggedSprite != null && player.gold >= draggedSprite.cost) {
                        player.gold -= draggedSprite.cost
                        updateStatsDisplay()

                        box.clearChildren()
                        sourceBox.clearChildren()
                        addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                        box.setUserObject("sprite", draggedSprite)
                        setupDragSourceForUnitBox(box, boxSize)

                    } else {
                        currencyLabel.style.fontColor = Color.RED
                        Timer.schedule(object : Timer.Task() {
                            override fun run() {
                                currencyLabel.style.fontColor = Color.WHITE
                            }
                        }, 0.5f)
                    }
                }
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                box.background = spriteFrame
            }
        }
    }

    // Helper method to add a sprite with its stats to a box
    private fun addSpriteToBox(box: Table, sprite: Sprite, drawable: Drawable) {
        val stack = Stack()
        box.add(stack).width(spriteSize).height(spriteSize)

        // Add sprite image
        val spriteImage = Image(drawable)
        spriteImage.setScaling(Scaling.fit)
        stack.add(spriteImage)

        // Add stat overlay
        val statOverlay = Table()
        statOverlay.setFillParent(true)
        statOverlay.top().left().padTop(90f)

        // HP stack (heart + number)
        val hpStack = Stack()
        val heartIcon = Image(TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("heart.png")))))
        heartIcon.setSize(55f, 55f)

        val hpLabel = Label(sprite.health.toString(), skin)
        hpLabel.setFontScale(1.7f)
        hpLabel.setAlignment(Align.center)
        hpLabel.style.background = null

        val hpLabelContainer = Container(hpLabel)
        hpLabelContainer.padLeft(-7f)

        hpStack.add(heartIcon)
        hpStack.add(hpLabelContainer)

        // ATK stack (swords + number)
        val atkStack = Stack()
        val atkIcon = Image(TextureRegionDrawable(TextureRegion(Texture(Gdx.files.internal("crossed_swords.png")))))
        atkIcon.setSize(55f, 55f)

        val atkLabel = Label(sprite.attack.toString(), skin)
        atkLabel.setFontScale(1.7f)
        atkLabel.setAlignment(Align.center)
        atkLabel.style.background = null

        val atkLabelContainer = Container(atkLabel)
        atkLabelContainer.padLeft(-7f)

        atkStack.add(atkIcon)
        atkStack.add(atkLabelContainer)

        // Add both stacks to stat overlay
        statOverlay.add(hpStack).size(55f).padLeft(-30f).padBottom(20f)
        statOverlay.add().expandX() // flexible spacer between
        statOverlay.add(atkStack).size(55f).padRight(-40f).padBottom(30f).right()

        stack.add(statOverlay)
    }

    // Set up a drag source for a single unit box
    private fun setupDragSourceForUnitBox(box: Table, boxSize: Float) {
        // Only add drag source if the box has an image/stack and it's not empty
        if (box.children.size > 0) {
            val child = box.children.first()
            val sprite = box.getUserObject("sprite") as? Sprite

            // Only proceed if we have both visual and data components
            if (sprite != null && child is Stack) {
                // Find the image within the stack
                val image = child.children.find { it is Image } as? Image

                if (image != null && image.drawable != null && image.drawable != emptyDrawable) {
                    // Add new drag source
                    dragAndDrop.addSource(object : DragAndDrop.Source(image) {
                        override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                            val payload = DragAndDrop.Payload()
                            val dragActor = Image(image.drawable)
                            dragActor.setSize(boxSize, boxSize)
                            payload.dragActor = dragActor

                            // Store info about source, its content, and the sprite
                            payload.`object` = DragPayload(
                                image.drawable as TextureRegionDrawable,
                                box,
                                sprite
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
        // Check if player has enough coins for reroll (typically costs 1)
        if (player.gold >= 1) {
            playerController.reroll()
            updateStatsDisplay()

            shopTable.clearChildren()

            // Use the sprites from the shop model
            shop.slots.forEachIndexed { index, unit ->
                val shopBox = Table()
                shopBox.background = spriteFrame

                if (unit is Sprite) {
                    // Get the appropriate drawable for this sprite
                    val key = unit.name + "-" + unit.color
                    val spriteDrawable = spriteTextures[key] ?: emptyDrawable

                    // Add the sprite to the box with its stats
                    addSpriteToBox(shopBox, unit, spriteDrawable)

                    shopTable.add(shopBox).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)

                    // Register drag source for this shop item
                    val image = (shopBox.children.first() as Stack).children.find { it is Image } as Image
                    dragAndDrop.addSource(object : DragAndDrop.Source(image) {
                        override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                            val payload = DragAndDrop.Payload()
                            val dragActor = Image(image.drawable)
                            dragActor.setSize(boxSize, boxSize)
                            payload.dragActor = dragActor

                            // Store sprite data in payload
                            payload.`object` = DragPayload(
                                spriteDrawable,
                                shopBox,
                                unit
                            )

                            image.color.a = 0.5f
                            return payload
                        }

                        override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?, target: DragAndDrop.Target?) {
                            image.color.a = 1f
                        }
                    })
                }
            }
        } else {
            // Not enough coins for reroll - provide visual feedback
            currencyLabel.style.fontColor = Color.RED
            // Schedule to reset color after a delay
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    currencyLabel.style.fontColor = Color.WHITE
                }
            }, 0.5f)
        }
    }

    // Update the stats display labels
    private fun updateStatsDisplay() {
        currencyLabel.setText(player.gold)
        println(player.gold)
        //heartLabel.setText(playerHealth.toString())
        trophyLabel.setText("10")
    }

    // Helper extension function to store and retrieve different types of user objects
    private fun Table.setUserObject(key: String, value: Any?) {
        val userData = this.userObject as? MutableMap<String, Any?> ?: mutableMapOf()
        userData[key] = value
        this.userObject = userData
    }

    private fun Table.getUserObject(key: String): Any? {
        return (this.userObject as? MutableMap<String, Any?>)?.get(key)
    }

    // Add this new method to load textures
    private fun loadSpriteTextures() {
        // Load all available sprite textures
        val spritesJson = Gdx.files.internal("sprites.json").readString()
        val sprites = parseSpritesFromJson(spritesJson)

        // Create texture drawables for each unique sprite
        sprites.forEach { sprite ->
            val texturePath = sprite.path
            if (!spriteTextures.containsKey(sprite.name + "-" + sprite.color)) {
                try {
                    val texture = Texture(Gdx.files.internal(texturePath))
                    spriteTextures[sprite.name + "-" + sprite.color] = TextureRegionDrawable(texture)
                } catch (e: Exception) {
                    Gdx.app.error("TextureLoading", "Failed to load texture: $texturePath", e)
                    // Fallback to empty texture
                    spriteTextures[sprite.name + "-" + sprite.color] = emptyDrawable
                }
            }
        }
    }

    private fun parseSpritesFromJson(json: String): List<Sprite> {
        val sprites = mutableListOf<Sprite>()

        // Use gdx-json to parse the JSON
        val jsonReader = com.badlogic.gdx.utils.JsonReader()
        val jsonValue = jsonReader.parse(json)

        for (i in 0 until jsonValue.size) {
            val spriteJson = jsonValue.get(i)

            val sprite = Sprite().apply {
                name = spriteJson.getString("name", "unknown")
                attack = spriteJson.getInt("attack", 1)
                health = spriteJson.getInt("health", 1)
                tier = spriteJson.getInt("tier", 1)
                level = spriteJson.getInt("level", 1)
                cost = spriteJson.getInt("cost", 3)
                isFrozen = spriteJson.getBoolean("isFrozen", false)
                color = spriteJson.getString("color", "base")
                path = spriteJson.getString("path", "empty.png")
            }

            sprites.add(sprite)
        }

        return sprites
    }

    // Add a method to initialize the shop
    private fun initializeShop() {
        // Read sprites from JSON
        val spritesJson = Gdx.files.internal("sprites.json").readString()
        val availableSprites = parseSpritesFromJson(spritesJson)

        // Populate shop with random selection of sprites
        val shopSprites = availableSprites.filter { it.color == "base" }.shuffled().take(4)

        // Add sprites to shop slots
        shop.slots.clear()
        shopSprites.forEach { sprite ->
            // Create a copy of the sprite to avoid reference issues
            val shopSprite = sprite.copy()
            shop.slots.add(shopSprite as GameUnit)
        }
    }

    override fun show() {
        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage
        Gdx.app.log("DEBUG", "File exists? " + Gdx.files.internal("uiskin.json").exists())
        player = Player()
        shop = Shop()
        player.shop = shop
        shopController = ShopController(player)
        player.shopController = shopController
        player.team = Team()
        playerController = PlayerController(player)

        // Load all sprite textures from the sprites.json data
        loadSpriteTextures()

        // Initialize the shop with sprites
        initializeShop()



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
        currencyLabel = Label(player.gold.toString(), skin, "default")
        currencyLabel.setFontScale(fontScale)
        currencyLabel.style.background = statBackground
        miniTable.add(currencyLabel).padLeft(spaceBetweenObjects)

        //miniTable.add(heartIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        heartLabel = Label(playerHealth.toString(), skin, "default")
        heartLabel.setFontScale(fontScale)
        heartLabel.style.background = statBackground
        //miniTable.add(heartLabel).padLeft(spaceBetweenObjects)

        miniTable.add(hourglassIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        hourglassLabel = Label(playerTurn.toString(), skin, "default")
        hourglassLabel.setFontScale(fontScale)
        hourglassLabel.style.background = statBackground
        miniTable.add(hourglassLabel).padLeft(spaceBetweenObjects)

        miniTable.add(trophyIcon).size(iconSize, iconSize).padLeft(spaceBetweenObjects)
        trophyLabel = Label(playerTrophies.toString(), skin, "default")
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
            // Tag the box as belonging to the unit table
            box.setUserObject("type","unitBox")
            box.background = spriteFrame

            // Add a placeholder image
            val placeholder = Container<Image>(Image(emptyDrawable))
            box.add(placeholder).width(spriteSize).height(spriteSize)

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
        itemContainer.background = spriteFrame

        // Create table for items
        val itemTable = Table()
        itemTable.setPosition(1250f, 240f)
        itemTable.pad(10f)

        // Create item boxes (these would be integrated with Item class later)
        for (i in 1..2) {
            val box = Table()
            box.background = spriteFrame

            // Create an image using the unit sprite
            val unitImage = Image(birdDrawable)
            unitImage.setScaling(Scaling.fit)
            box.add(unitImage).width(spriteSize).height(spriteSize).pad(20f)

            // Add box to the row
            itemTable.add(box).width(boxSize).height(boxSize).padRight(20f).padBottom(20f)
        }

        // Place shopUnitTable in a container for better control
        val shopUnitContainer = Container(unitTable)
        shopUnitContainer.setSize(320f, 320f)
        shopUnitContainer.setPosition(50f, 200f)
        shopUnitContainer.background = spriteFrame
        shopUnitContainer.pad(20f)

        // Shop Unit Table
        val shopUnitTable = Table()
        shopUnitTable.setPosition(630f, 240f)

        // Initial population of shop
        rerollShop(shopUnitTable, boxSize)

        val rerollTable = Table()
        rerollTable.setPosition(130f, 230f)

        rerollBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                rerollShop(shopUnitTable, boxSize)
            }
        })

        // Add to table
        rerollTable.add(rerollBtn).width(boxSize).height(boxSize).pad(30f)

        // Create button for starting the battle
        // Load your custom texture
        val startBattleTexture = Texture(Gdx.files.internal("startbattlebtn.png"))

// Create drawable from the texture
        val startBattleDrawable = TextureRegionDrawable(TextureRegion(startBattleTexture))

// Create the ImageButton with your custom image
        val startBattleBtn = ImageButton(startBattleDrawable)

// Add click listener to handle the screen switch
        startBattleBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.screen = GameScreen(game)
            }
        })

// Add the button to the table
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
        // Implement pause functionality if needed
    }

    override fun resume() {
        // Implement resume functionality if needed
    }

    override fun hide() {
        Gdx.input.inputProcessor = null
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }

    // Extension function for Sprite to create a deep copy
    private fun Sprite.copy(): Sprite {
        return Sprite().apply {
            this.name = this@copy.name
            this.attack = this@copy.attack
            this.health = this@copy.health
            this.tier = this@copy.tier
            this.level = this@copy.level
            this.cost = this@copy.cost
            this.isFrozen = this@copy.isFrozen
            this.color = this@copy.color
            // Item would be copied here when implemented
        }
    }
}
