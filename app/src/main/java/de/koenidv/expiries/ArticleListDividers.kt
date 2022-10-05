package de.koenidv.expiries

import org.threeten.bp.LocalDate

class ArticleListDividers {

    fun determineAddDividers(
        before: LocalDate,
        after: LocalDate,
        today: LocalDate = LocalDate.now()
    ): Boolean {
        if (bothPast(before, after, today)) return false
        if (onePast(before, after, today)) return true
        // Dates are today or later
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

    private fun bothPast(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today && after < today

    private fun onePast(before: LocalDate, after: LocalDate, today: LocalDate) =
        before < today || after < today

    private fun bothThisWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        before.getWeekNumber() == today.getWeekNumber() && after.getWeekNumber() == today.getWeekNumber()

    private fun bothNextWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        before.getWeekNumber() == today.getWeekNumber() + 1 && after.getWeekNumber() == today.getWeekNumber() + 1

    private fun oneThisOrNextWeek(before: LocalDate, after: LocalDate, today: LocalDate) =
        (before.getWeekNumber() == today.getWeekNumber() || after.getWeekNumber() == today.getWeekNumber()) ||
                (before.getWeekNumber() == today.getWeekNumber() + 1 || after.getWeekNumber() == today.getWeekNumber() + 1)

    private fun bothFarFuture(before: LocalDate, after: LocalDate, today: LocalDate) =
        today.until(before).months >= 3 && today.until(after).months >= 3

    private fun bothSameMonth(before: LocalDate, after: LocalDate) =
        before.monthValue == after.monthValue

}