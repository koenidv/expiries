package de.koenidv.expiries

import com.google.gson.JsonElement
import org.threeten.bp.LocalDate

class ArticleParser {

    fun parseString(string: String): JsonElement = com.google.gson.JsonParser.parseString(string)

    fun parseArticle(json: JsonElement): Article {
        val data = json.asJsonObject
        val product = data.getAsJsonObject("product")
        return Article(
            product["code"].asString,
            product["product_name"].asString,
            null,
            product["image_small_url"].asString,
            null,
            LocalDate.now(),
            1f,
            "PIECE"
        )
    }
}