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
import io.github.super_auto_pets.models.Empty

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
    private lateinit var player: Player
    private lateinit var playerController: PlayerController
    private lateinit var team: Team

    //Shop
    private lateinit var shop: Shop
    private lateinit var shopController: ShopController
    private lateinit var shopUnitTable: Table

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

    // Debug utility
    private fun debugShopContents() {
        Gdx.app.log("DEBUG", "Shop slots size: ${player.shop.slots.size}")
        for (i in player.shop.slots.indices) {
            Gdx.app.log("DEBUG", "Slot $i: ${player.shop.slots[i]}")
        }
    }

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
                        // Get the team position for the source and target boxes
                        val sourceIndex = getTeamPositionFromBox(sourceBox)
                        val targetIndex = getTeamPositionFromBox(box)

                        if (sourceIndex >= 0 && targetIndex >= 0) {
                            // Use PlayerController to move the unit
                            val result = playerController.move(sourceIndex, targetIndex)

                            if (result >= 0) {
                                // Move succeeded - update UI
                                box.clearChildren()
                                addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                                box.setUserObject("sprite", draggedSprite)
                                setupDragSourceForUnitBox(box, boxSize)

                                sourceBox.clearChildren()
                                val placeholder = Container(Image(emptyDrawable))
                                sourceBox.add(placeholder).width(spriteSize).height(spriteSize)
                                sourceBox.setUserObject("type","unitBox")
                            }
                        } else {
                            // Just update the UI if we can't find team indices
                            box.clearChildren()
                            addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                            box.setUserObject("sprite", draggedSprite)
                            setupDragSourceForUnitBox(box, boxSize)

                            sourceBox.clearChildren()
                            val placeholder = Container(Image(emptyDrawable))
                            sourceBox.add(placeholder).width(spriteSize).height(spriteSize)
                            sourceBox.setUserObject("type","unitBox")
                        }
                    }
                } else {
                    // Buying from the shop
                    if (draggedSprite != null && player.gold >= draggedSprite.cost) {
                        // Debug before buying
                        debugShopContents()

                        // Get the shop index from the source box
                        val shopIndex = sourceBox.getUserObject("shopIndex") as? Int ?: -1
                        Gdx.app.log("DEBUG", "Attempting to buy from shop index: $shopIndex")

                        // Find the team position for the target box
                        val teamPosition = getTeamPositionFromBox(box)
                        Gdx.app.log("DEBUG", "Placing into team position: $teamPosition")

                        if (shopIndex >= 0 && teamPosition >= 0 && shopIndex < player.shop.slots.size) {
                            try {
                                // Use PlayerController to buy the unit
                                val result = playerController.buy(shopIndex, teamPosition)
                                Gdx.app.log("DEBUG", "Buy result: $result")

                                if (result >= 0) {
                                    // Buy succeeded - update UI
                                    box.clearChildren()
                                    addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                                    box.setUserObject("sprite", draggedSprite)
                                    setupDragSourceForUnitBox(box, boxSize)

                                    // Update shop UI
                                    populateShopFromModel(shopUnitTable, boxSize)
                                    updateStatsDisplay()
                                } else {
                                    // Buy failed - provide visual feedback
                                    currencyLabel.style.fontColor = Color.RED
                                    Timer.schedule(object : Timer.Task() {
                                        override fun run() {
                                            currencyLabel.style.fontColor = Color.WHITE
                                        }
                                    }, 0.5f)
                                }
                            } catch (e: Exception) {
                                Gdx.app.error("ERROR", "Exception buying unit: ${e.message}")
                                e.printStackTrace()

                                // Use a fallback approach - just update UI
                                player.gold -= draggedSprite.cost

                                box.clearChildren()
                                addSpriteToBox(box, draggedSprite, dragPayload.drawable)
                                box.setUserObject("sprite", draggedSprite)
                                setupDragSourceForUnitBox(box, boxSize)

                                updateStatsDisplay()
                            }
                        } else {
                            // Invalid indices - provide feedback
                            Gdx.app.error("ERROR", "Invalid shop/team indices: shop=$shopIndex, team=$teamPosition, shopSize=${player.shop.slots.size}")
                            currencyLabel.style.fontColor = Color.RED
                            Timer.schedule(object : Timer.Task() {
                                override fun run() {
                                    currencyLabel.style.fontColor = Color.WHITE
                                }
                            }, 0.5f)
                        }
                    } else {
                        // Not enough gold - provide visual feedback
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

    // Helper method to get the team position from a box
    private fun getTeamPositionFromBox(box: Table): Int {
        return box.getUserObject("teamIndex") as? Int ?: -1
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
            if (box is Table && box.getUserObject("type") == "unitBox") {
                setupDragSourceForUnitBox(box, boxSize)
            }
        }
    }

    // Method to populate shop from the model
    private fun populateShopFromModel(shopTable: Table, boxSize: Float) {
        shopTable.clearChildren()

        Gdx.app.log("DEBUG", "Populating shop UI from model. Slots size: ${player.shop.slots.size}")

        // Loop through the actual shop.slots collection
        player.shop.slots.forEachIndexed { index, unit ->
            val shopBox = Table()
            shopBox.background = spriteFrame

            Gdx.app.log("DEBUG", "Processing shop slot $index: $unit")

            if (unit is Sprite) {
                // Get the appropriate drawable for this sprite
                val key = unit.name + "-" + unit.color
                val spriteDrawable = spriteTextures[key] ?: emptyDrawable

                // Add the sprite to the box with its stats
                addSpriteToBox(shopBox, unit, spriteDrawable)

                // Store unit reference and its index in the shop model
                shopBox.setUserObject("sprite", unit)
                shopBox.setUserObject("shopIndex", index)
                shopBox.setUserObject("unitType", "animal")

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
            } else if (unit is Item) {
                // Handle Item UI similarly to sprites
                // This part would need to be implemented based on your Item rendering logic
                // ...

                shopBox.setUserObject("unitType", "item")
                shopBox.setUserObject("shopIndex", index)
            }
        }
    }

    private fun rerollShop(shopTable: Table, boxSize: Float) {
        // Check if player has enough coins for reroll
        if (player.gold >= 1) {
            // Use the controller to reroll the actual shop model
            val result = playerController.reroll()

            if (result >= 0) {
                // Success - update the UI based on the new shop contents
                populateShopFromModel(shopTable, boxSize)
                updateStatsDisplay()
            } else {
                // Reroll failed - provide visual feedback
                currencyLabel.style.fontColor = Color.RED
                Timer.schedule(object : Timer.Task() {
                    override fun run() {
                        currencyLabel.style.fontColor = Color.WHITE
                    }
                }, 0.5f)
            }
        } else {
            // Not enough coins for reroll - provide visual feedback
            currencyLabel.style.fontColor = Color.RED
            Timer.schedule(object : Timer.Task() {
                override fun run() {
                    currencyLabel.style.fontColor = Color.WHITE
                }
            }, 0.5f)
        }
    }

    // Update the stats display labels
    private fun updateStatsDisplay() {
        currencyLabel.setText(player.gold.toString())
        hourglassLabel.setText(playerTurn.toString())
        trophyLabel.setText(playerTrophies.toString())
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

    override fun show() {
        // Input goes to our stage so buttons can be clicked
        Gdx.input.inputProcessor = stage
        Gdx.app.log("DEBUG", "File exists? " + Gdx.files.internal("uiskin.json").exists())

        // FIXED INITIALIZATION ORDER:
        shop = Shop()
        Gdx.app.log("DEBUG", "Created shop. Initial size: ${shop.slots.size}")

        // 2. Create a new player WITHOUT initializing ShopController yet
        player = Player()
        Gdx.app.log("DEBUG", "Created player. Player's initial shop size: ${player.shop.slots.size}")

        // 3. Set our custom shop to the player
        player.shop = shop
        Gdx.app.log("DEBUG", "Set custom shop to player. Player's shop size now: ${player.shop.slots.size}")
        // 4. Now create a ShopController with the player that already has our shop
        shopController = ShopController(player)

        // 5. Set the shop controller to the player
        player.shopController = shopController

        // 6. Create and set up the team

        // 7. Set up the player controller
        playerController = PlayerController(player)

        // Log the shop size at this point
        Gdx.app.log("DEBUG", "After initialization - Shop slots size: ${player.shop.slots.size}")

        // Load all sprite textures from the sprites.json data
        loadSpriteTextures()

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
        for (i in 0 until 4) {
            val box = Table()
            // Tag the box as belonging to the unit table
            box.setUserObject("type","unitBox")
            box.setUserObject("teamIndex", i)  // Store the team index
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

        // Shop Unit Table - Now using the class property
        shopUnitTable = Table()
        shopUnitTable.setPosition(630f, 240f)

        // Initial population of shop from the model
        debugShopContents() // Debug before populating UI
        populateShopFromModel(shopUnitTable, boxSize)

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

        // Update the stats display initially
        updateStatsDisplay()
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
