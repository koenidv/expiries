package de.koenidv.expiries

import com.google.gson.JsonParser
import org.junit.Test

internal class JsonParserTest {

    private fun getResource(name: String): String {
        return JsonParserTest::class.java.classLoader!!.getResource(name)!!.readText()
    }

    @Test
    fun parseArticleNameTest() {
        val json = JsonParser.parseString(getResource("OFFResponseExample1.json"))
        val parsed: Article = ArticleParser().parseArticle(json)
        assert(parsed.name == "Feiner Gurkensalat in Joghurtdressing")
    }

    @Test
    fun parseArticleImageTest() {
        val json = JsonParser.parseString(getResource("OFFResponseExample2.json"))
        val parsed: Article = ArticleParser().parseArticle(json)
        assert(parsed.image_url == "https://images.openfoodfacts.org/images/products/90162480/front_de.9.200.jpg")
    }


}