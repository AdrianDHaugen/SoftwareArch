package io.github.some_example_name.utilities

import com.beust.klaxon.Klaxon
import io.github.some_example_name.controller.itemsDB
import io.github.some_example_name.controller.spritesDB
import io.github.some_example_name.models.Item
import io.github.some_example_name.models.Sprite
import java.io.File

class JsonParser {
    fun parseSprites(): List<Sprite> {
        val file1 = "core\\src\\main\\kotlin\\io\\github\\some_example_name\\resources\\sprites.json"
        val fileContent = File(file1).readText()

        spritesDB = Klaxon().parseArray<Sprite>(fileContent)!!

        return spritesDB
    }

    fun parseItems(): List<Item> {
        val file2 = "core\\src\\main\\kotlin\\io\\github\\some_example_name\\resources\\items.json"
        val fileContent = File(file2).readText()

        itemsDB = Klaxon().parseArray<Item>(fileContent)!!

        return itemsDB
    }


    fun main() {
        val sprites = parseSprites()
        val itemsDB = parseItems()

        for (sprite in sprites) {
            println("Name: ${sprite.name}")
        }
        for (item in itemsDB) {
            println("Name: ${item.name}")
        }

    }
}
