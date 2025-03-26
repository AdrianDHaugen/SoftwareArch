class User(val name: String, val agent: MessageAgent) {
    var gold: Int = 10
    var turn: Int = 1
    val team = Team()
    val shop = Shop()
    val shopController = ShopController(agent)

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
        return shopController.toggleFreeze(pos)
    }

    fun reroll(): Int {
        return shopController.reroll()
    }

    fun buy(itemPos: Int, targetPos: Int): Int {
        return shopController.buy(itemPos, targetPos)
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
