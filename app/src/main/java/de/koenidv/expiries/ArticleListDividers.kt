package de.koenidv.expiries

import org.threeten.bp.LocalDate

class ArticleListDividers {

    fun addListDividers(list: List<Article>): MutableList<ListItem> {
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
        return result.toMutableList()
    }

    fun determineAddDividers(
        before: LocalDate, after: LocalDate, today: LocalDate = LocalDate.now()
    ) = when {
        before == after -> false
        bothPast(before, after, today) -> false
        onePast(before, after, today) -> true
        // Dates are today or later
        oneTomorrow(before, after, today) -> true
        bothThisWeek(before, after, today) -> false
        bothNextWeek(before, after, today) -> false
        // Dates are not in the same this or next week
        oneThisOrNextWeek(before, after, today) -> true
        bothFarFuture(before, after, today) -> false
        // At least one date is less than 3 months from today
        bothSameMonth(before, after) -> false
        // Days are not in the same this or next 1-2 months
        else -> true
    }

    fun resolveDividerDate(date: LocalDate, today: LocalDate) = when {
        date.isBefore(today) -> R.string.timeframe_expired
        isToday(date, today) -> R.string.timeframe_today
        isTomorrow(date, today) -> R.string.timeframe_tomorrow
        isThisWeek(date, today) -> R.string.timeframe_this_week
        isNextWeek(date, today) -> R.string.timeframe_next_week
        bothSameMonth(date, today) -> R.string.timeframe_this_month
        bothSameMonth(date, today.plusMonths(1)) -> R.string.timeframe_next_month
        else -> R.string.timeframe_later
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