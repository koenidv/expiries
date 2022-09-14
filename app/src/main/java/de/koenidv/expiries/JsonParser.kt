package de.koenidv.expiries

import org.json.JSONObject

class JsonParser {
    fun parseArticle(json: JSONObject): Article {
        val imageUrl = json.getJSONObject("product").optString("image_small_url")
        return Article(
            json.getString("code"),
            json.getJSONObject("product").getString("product_name"),
            null,
            if (imageUrl != "") imageUrl else null,
            null
        )
    }
}