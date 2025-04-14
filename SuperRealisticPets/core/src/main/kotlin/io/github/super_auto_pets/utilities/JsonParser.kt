package io.github.super_auto_pets.utilities

import com.beust.klaxon.Klaxon
import io.github.super_auto_pets.controller.itemsDB
import io.github.super_auto_pets.controller.spritesDB
import io.github.super_auto_pets.models.Item
import io.github.super_auto_pets.models.Sprite
import java.io.File

class JsonParser {
    fun parseSprites(): List<Sprite> {
        val file1 = "core\\src\\main\\kotlin\\io\\github\\super_auto_pets\\resources\\sprites.json"
        val fileContent = File(file1).readText()

        spritesDB = Klaxon().parseArray<Sprite>(fileContent)!!

        return spritesDB
    }

    fun parseItems(): List<Item> {
        val file2 = "core\\src\\main\\kotlin\\io\\github\\super_auto_pets\\resources\\items.json"
        val fileContent = File(file2).readText()

        itemsDB = Klaxon().parseArray<Item>(fileContent)!!

        return itemsDB
    }

}
