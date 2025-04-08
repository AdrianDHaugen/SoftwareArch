package io.github.some_example_name

import com.beust.klaxon.Klaxon
import java.io.File

class JsonParser {
    fun parseSprites(): List<Sprite> {
        val file1 = "core\\src\\main\\kotlin\\io\\github\\some_example_name\\sprites.json"
        val fileContent = File(file1).readText()

        spritesDB = Klaxon().parseArray<Sprite>(fileContent)!!

        return spritesDB
    }

    fun parseItems(): List<Item> {
        val file2 = "core\\src\\main\\kotlin\\io\\github\\some_example_name\\items.json"
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
