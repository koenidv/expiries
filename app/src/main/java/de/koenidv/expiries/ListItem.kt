package de.koenidv.expiries

import org.threeten.bp.LocalDate

data class ListItem(
    val type: Int,
    val article_data: Article?,
    val divider_data: LocalDate?
) {
    constructor(article: Article) : this(TYPE_ARTICLE, article, null)
    constructor(date: LocalDate) : this(TYPE_DIVIDER, null, date)

    companion object {
        const val TYPE_ARTICLE = 0
        const val TYPE_DIVIDER = 1
    }
}
