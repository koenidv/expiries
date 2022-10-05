package de.koenidv.expiries

import org.threeten.bp.LocalDate

class ArticleListDividers {

    fun addListDividers(list: List<Article>): List<ListItem> {
        val result = ArrayList<ListItem>()
        val today = LocalDate.now()
        list.forEachIndexed { index, article ->
            if (article.expiry != null &&
                (index == 0 ||
                        list[index - 1].expiry == null ||
                        determineAddDividers(
                            article.expiry,
                            list[index - 1].expiry!!,
                            today
                        ))
            ) {
                result.add(ListItem(article.expiry))
            }
            result.add(ListItem(article))
        }
        return result.toList()
    }

    fun determineAddDividers(
        before: LocalDate,
        after: LocalDate,
        today: LocalDate = LocalDate.now()
    ): Boolean {
        if (bothPast(before, after, today)) return false
        if (onePast(before, after, today)) return true
        // Dates are today or later
        if (oneTomorrow(before, after, today)) return true
        if (bothThisWeek(before, after, today)) return false
        if (bothNextWeek(before, after, today)) return false
        // Dates are not in the same this or next week
        if (oneThisOrNextWeek(before, after, today)) return true
        if (bothFarFuture(before, after, today)) return false
        // At least one date is less than 3 months from today
        if (bothSameMonth(before, after)) return false
        // Days are not in the same this or next 1-2 months
        return true
    }

    fun resolveDividerDate(date: LocalDate, today: LocalDate): Int {
        return 0
    }

    private fun bothPast(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today && after < today

    private fun onePast(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today || after < today

    private fun isToday(date: LocalDate, today: LocalDate) =
        date == today

    private fun isTomorrow(date: LocalDate, today: LocalDate) =
        date == today.plusDays(1)

    private fun oneTomorrow(before: LocalDate, after: LocalDate, today: LocalDate) =
        isTomorrow(before, today) || isTomorrow(after, today)

    private fun isThisWeek(date: LocalDate, today: LocalDate) =
        date.getWeekNumber() == today.getWeekNumber() && date.year == today.year

    private fun bothThisWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        isThisWeek(before, today) && isThisWeek(after, today)

    private fun isNextWeek(date: LocalDate, today: LocalDate) =
        date.getWeekNumber() == today.getWeekNumber() + 1 && date.year == today.year

    private fun bothNextWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        isNextWeek(before, today) && isNextWeek(after, today)

    private fun oneThisOrNextWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        (before.getWeekNumber() == today.getWeekNumber() || after.getWeekNumber() == today.getWeekNumber()) ||
                (before.getWeekNumber() == today.getWeekNumber() + 1 || after.getWeekNumber() == today.getWeekNumber() + 1)

    private fun bothFarFuture(before: LocalDate, after: LocalDate, today: LocalDate) =
        today.until(before).months >= 2 && today.until(after).months >= 2

    private fun bothSameMonth(before: LocalDate, after: LocalDate) =
        before.monthValue == after.monthValue

}