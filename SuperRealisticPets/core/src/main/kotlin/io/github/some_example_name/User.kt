class User(val name: String, val agent: MessageAgent) {
    var gold: Int = 10
    var turn: Int = 1
    val team = Team()
    val shop = Shop()

    init {
        agent.setUser(this)
    }

    fun startTurn() {
        agent.loadBackup()
        agent.resetTempStats()
        turn++
        gold = 10
        shop.startTurn()
        agent.enqueueEvent(EventNames.START_TURN)
        agent.handleEvents()
    }

    fun toggleFreeze(pos: Int): Int {
        val shopSlot = shop.getSlot(pos)
        return if (shopSlot.item is Empty || shopSlot.item is Unarmed) {
            -1
        } else {
            shop.toggleFreeze(pos)
            0
        }
    }

    fun reroll(): Int {
        return if (gold < 1) {
            -1
        } else {
            gold--
            shop.reroll()
            0
        }
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        val shopSlot = shop.roster[itemPos]

        if (shopSlot.item is Empty || shopSlot.item is Unarmed) {
            return -1
        }

        if (gold < shopSlot.item.cost) {
            return -1
        }

        return when (shopSlot.item) {
            is Animal -> buyAnimalResponse(shopSlot, targetPos)
            is Equipment -> buyFoodResponse(shopSlot, targetPos)
            else -> -1
        }
    }

    private fun buyAnimalResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        return when (val targetAnimal = team[targetPos]) {
            is Empty -> buyToEmptyResponse(shopSlot, targetPos)
            shopSlot.item::class -> buyToSameResponse(shopSlot, targetPos)
            else -> buyDifferentAnimalResponse(shopSlot, targetPos)
        }
    }

    private fun buyToEmptyResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        gold -= shopSlot.item.cost
        shopSlot.buy()
        team[targetPos] = shopSlot.item as Animal
        agent.enqueueEvent(EventNames.FRIEND_SUMMONED_SHOP, targetPos)
        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, targetPos)
        agent.enqueueEvent(EventNames.BUY, targetPos)
        if ((shopSlot.item as Animal).tier == 1) {
            agent.enqueueEvent(EventNames.BUY_T1_PET, targetPos)
        }
        return 0
    }

    private fun buyToSameResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        val targetUnit = team[targetPos]
        val item = shopSlot.buy() as Animal
        gold -= item.cost
        targetUnit.increaseXp(1)
        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, targetPos)
        agent.enqueueEvent(EventNames.BUY, targetPos)
        if (item.tier == 1) {
            agent.enqueueEvent(EventNames.BUY_T1_PET, targetPos)
        }
        return 0
    }

    private fun buyDifferentAnimalResponse(shopSlot: ShopSlot, targetPos: Int): Int {
        if (!team.hasSummonSpace()) {
            return -1
        }
        gold -= shopSlot.item.cost
        team.summon(shopSlot.item as Animal, targetPos)
        agent.enqueueEvent(EventNames.FRIEND_SUMMONED_SHOP, targetPos)
        agent.enqueueEvent(EventNames.FRIEND_BOUGHT, targetPos)
        agent.enqueueEvent(EventNames.BUY, targetPos)
        return 0
    }

    fun sell(pos: Int): Int {
        val actor = team[pos]
        if (actor is Empty) {
            return -1
        }
        agent.enqueueEvent(EventNames.SELL, pos, removed = actor)
        agent.enqueueEvent(EventNames.FRIEND_SOLD, pos)
        gold += actor.level
        team[pos] = Empty()
        agent.handleEvents()
        return 0
    }

    fun move(rosterInit: Int, rosterFinal: Int): Int {
        if (team[rosterInit] is Empty || rosterInit == rosterFinal) {
            return -1
        }
        val movedAnimal = team[rosterInit]
        team[rosterInit] = Empty()
        team.move(rosterInit, rosterFinal, movedAnimal)
        return 0
    }

    fun combine(rosterInit: Int, rosterFinal: Int): Int {
        val anim1 = team[rosterInit]
        val anim2 = team[rosterFinal]

        if (anim1 is Empty || anim2 is Empty || anim1::class != anim2::class) {
            return -1
        }

        anim2.mergeStats(anim1)
        anim1.xp = 0
        team[rosterInit] = Empty()

        agent.handleEvents()
        return 0
    }

    fun endTurn() {
        agent.enqueueEvent(EventNames.END_TURN)
        agent.handleEvents()
    }
}
