package de.koenidv.expiries

import org.threeten.bp.LocalDate

data class ListDivider(
    val date: LocalDate
) : ListItem(TYPE_DIVIDER) {
    fun getDividerString() = ArticleListDividers().resolveDividerDate(date)
}
