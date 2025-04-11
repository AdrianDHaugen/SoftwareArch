package io.github.some_example_name

class Sprite : ISprite {

    private var _name: String = name
    override var name: String
        get() = _name
        set(value) { _name = value }

    private var _attack: Int = attack
    override var attack: Int
        get() = _attack
        set(value) { _attack = value }

    private var _health: Int = health
    override var health: Int
        get() = _health
        set(value) { _health = value }

    private var _tier: Int = tier
    override var tier: Int
        get() = _tier
        set(value) { _tier = value }

    private var _item: Item? = item
    override var item: Item?
        get() = _item
        set(value) { _item = value }

    private var _level: Int = level
    override var level: Int
        get() = _level
        set(value) { _level = value }

    private var _cost: Int = cost
    override var cost: Int
        get() = _cost
        set(value) { _cost = value }

    private var _isFrozen: Boolean = isFrozen
    override var isFrozen: Boolean
        get() = _isFrozen
        set(value) { _isFrozen = value }

    private var _color: String = color
    var color: String
        get() = _color
        set(value) { _color = value }


    override fun toString(): String {
        return "Sprite(name='$name', attack=$attack, health=$health, tier=$tier, item=$item, level=$level, cost=$cost, isFrozen=$isFrozen, color='$color')"
    }




    override fun onTurnStart() {
        item?.onTurnStart(this)
    }

    override fun onBattleStart() {
        item?.onBattleStart(this)
    }

    override fun attack(target: Sprite) {
        println("$name attacks ${target.name}!")
        target.health -= this.attack
        this.health -= target.attack
        item?.onAttack(this, target)
    }

    override fun mergeStats(sprite1: Sprite, sprite2: Sprite) {
        sprite1.attack += sprite2.attack
        sprite1.health += sprite2.health
        sprite1.tier += sprite2.tier
        sprite1.level += sprite2.level
    }

    override fun isAlive(): Boolean = health > 0


    //val fileContent = File("sprites.json").readText()


}
