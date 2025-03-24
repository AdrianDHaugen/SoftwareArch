class ShopController(private val agent: MessageAgent) {

    init {
        agent.setShopper(this)
    }

    /*
    fun startTurn() {
        agent.loadBackup()
        agent.resetTempStats()

        agent.turn++
        agent.gold = 10
        agent.shop.startTurn()

        agent.enqueueEvent(EventNames.START_TURN)
        agent.handleEvents()
    }
    THIS SHOULD BE HANDLED AT THE GAME CONTROLLER
    */

    fun toggleFreeze(pos: Int): Int {
        val shopSlot = agent.shop[pos]
        if (shopSlot.item is Empty || shopSlot.item is Unarmed) {
            return -1
        }
        agent.shop.toggleFreeze(pos)
        return 0
    }

    fun reroll(): Int {
        if (agent.gold < 1) return -1
        agent.shop.reroll()
        return 0
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        val shopSlot = agent.shop.slots[itemPos]
        if (shopSlot.item is Empty || shopSlot.item is Unarmed) {
            return -1
        }
        if (agent.gold < shopSlot.item.cost) {
            return -1
        }
        return when (shopSlot.item) {
            is Animal -> buyAnimalResponse(shopSlot, targetPos)
            is Equipment -> buyFoodResponse(shopSlot, targetPos)
            else -> -1
        }
    }

    private fun buyAnimalResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return when {
            agent.team.animals[targetPos] is Empty -> buyToEmptyResponse(shopSlot, targetPos)
            shopSlot.item::class == agent.team[targetPos]::class -> buyToSameResponse(shopSlot, targetPos)
            else -> buyDifferentAnimalResponse(shopSlot, targetPos)
        }.also { agent.handleEvents() }
    }

    private fun buyToEmptyResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        val target = Pair("team", targetPos)
        val item = shopSlot.item as Animal

        agent.gold -= item.cost
        shopSlot.buy()
        agent.team.animals[targetPos] = item

        agent.enqueueEvent(EventNames.FRIEND_SUMMONED_SHOP, actor = target)
        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, actor = target)
        agent.enqueueEvent(EventNames.BUY, actor = target)
        if (item.tier == 1) {
            agent.enqueueEvent(EventNames.BUY_T1_PET)
        }
        return 0
    }

    private fun buyToSameResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        val targetUnit = agent.team[targetPos]
        val target = Pair("team", targetPos)
        val item = shopSlot.buy() as Animal

        agent.gold -= item.cost
        targetUnit.increaseXp(1)

        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, actor = target)
        agent.enqueueEvent(EventNames.BUY, actor = target)
        if (item.tier == 1) {
            agent.enqueueEvent(EventNames.BUY_T1_PET, actor = target)
        }
        return 0
    }

    private fun buyDifferentAnimalResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        if (!agent.team.hasSummonSpace) return -1

        val actor = Pair("team", targetPos)
        val animal = shopSlot.item as Animal
        agent.gold -= animal.cost
        agent.team.summon(animal, targetPos)

        agent.enqueueEvent(EventNames.FRIEND_SUMMONED_SHOP, actor = actor)
        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, actor = actor)
        agent.enqueueEvent(EventNames.BUY, actor = actor)
        if (animal.tier == 1) {
            agent.enqueueEvent(EventNames.BUY_T1_PET, actor = actor)
        }
        return 0
    }

    private fun buyFoodResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return if ((shopSlot.item as Equipment).isTargeted) {
            buyTargetedFood(shopSlot, targetPos)
        } else {
            buyNonTargetedFood(shopSlot, targetPos)
        }.also { agent.handleEvents() }
    }

    private fun buyTargetedFood(shopSlot: ShopSlot, targetPos: Int): Int {
        if (agent.team[targetPos] is Empty) return -1

        val item = shopSlot.item as Equipment
        val actor = Pair("team", targetPos)
        agent.gold -= item.cost
        shopSlot.buy()

        agent.enqueueEvent(EventNames.BUY_FOOD)
        if (item.isHoldable) {
            agent.team[targetPos].held = item
        } else {
            agent.func[item.id]?.invoke(agent, Pair("team", targetPos), Pair("team", targetPos))
        }

        agent.enqueueEvent(EventNames.EAT_FOOD, actor = actor)
        agent.enqueueEvent(EventNames.FRIEND_EATS_FOOD, actor = actor)
        return 0
    }

    private fun buyNonTargetedFood(shopSlot: ShopSlot, targetPos: Int): Int {
        if (agent.team.size < 1) return -1
        val item = shopSlot.item

        agent.gold -= item.cost
        shopSlot.buy()
        agent.enqueueEvent(EventNames.BUY_FOOD)
        agent.func[item.id]?.invoke(agent, Pair("team", targetPos), Pair("team", targetPos))
        return 0
    }

    fun endTurn() {
        agent.enqueueEvent(EventNames.END_TURN)
        agent.handleEvents()
    }
}
