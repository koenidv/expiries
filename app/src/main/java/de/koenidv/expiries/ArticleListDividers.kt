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
                result.add(ListDivider(article.expiry))
            }
            result.add(article)
        }
        return result.toMutableList()
    }

    fun determineAddDividers(
        before: LocalDate, after: LocalDate, today: LocalDate = LocalDate.now()
    ) = when {
        before == after -> false
        bothLongPast(before, after, today) -> false
        bothRecent(before, after, today) -> false
        oneOrBothPast(before, after, today) -> true
        // Dates are today or later
        oneTodayOrTomorrow(before, after, today) -> true
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

    fun resolveDividerDate(date: LocalDate, today: LocalDate = LocalDate.now()) = when {
        isLongPast(date, today) -> R.string.timeframe_expired
        date.isBefore(today) -> R.string.timeframe_expired_recently
        isToday(date, today) -> R.string.timeframe_today
        isTomorrow(date, today) -> R.string.timeframe_tomorrow
        isThisWeek(date, today) -> R.string.timeframe_this_week
        isNextWeek(date, today) -> R.string.timeframe_next_week
        bothSameMonth(date, today) -> R.string.timeframe_this_month
        bothSameMonth(date, today.plusMonths(1)) -> R.string.timeframe_next_month
        else -> R.string.timeframe_later
    }

    private fun isLongPast(date: LocalDate, today: LocalDate) =
        date < today.minusDays(6)

    private fun bothLongPast(before: LocalDate, after: LocalDate, today: LocalDate) =
        isLongPast(before, today) && isLongPast(after, today)

    private fun bothRecent(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today && after < today && !isLongPast(before, today) && !isLongPast(after, today)

    private fun oneOrBothPast(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today || after < today || (before < today && after < today)

    private fun isToday(date: LocalDate, today: LocalDate) =
        date == today

    private fun isTomorrow(date: LocalDate, today: LocalDate) =
        date == today.plusDays(1)

    private fun oneTodayOrTomorrow(before: LocalDate, after: LocalDate, today: LocalDate) =
        isToday(before, today) || isToday(after, today) ||
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