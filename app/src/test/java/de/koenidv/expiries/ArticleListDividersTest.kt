package de.koenidv.expiries

import de.koenidv.expiries.Article.Companion.determineAddDividers
import org.junit.Test
import org.threeten.bp.LocalDate

class ArticleListDividersTest {

    private val T: Long = 16269L

    // Using epochDays to store dates: Tue, Oct 4, 2022 is epochDay T = 19269
    private fun testDates(day1: Long, day2: Long): Boolean {
        val today = LocalDate.ofEpochDay(T)
        val date1 = LocalDate.ofEpochDay(day1)
        val date2 = LocalDate.ofEpochDay(day2)
        return determineAddDividers(date1, date2, today)
    }

    @Test
    fun testDatesBefore() = assert(!testDates(T - 5, T - 2))

    @Test
    fun testDatesBeforeAfter() = assert(testDates(T - 2, T))

    @Test
    fun testDatesWeek() = assert(!testDates(T, T + 5))

    @Test
    fun testDatesWeekNextWeek() = assert(testDates(T + 5, T + 6))

    @Test
    fun testDatesNextWeek() = assert(!testDates(T + 6, T + 12))

    @Test
    fun testDatesNextWeekMonth() = assert(testDates(T + 12, T + 13))

    @Test
    fun testDatesMonth() = assert(!testDates(T + 13, T + 27))

    @Test
    fun testDatesMonthNextMonth() = assert(testDates(T + 28, T + 57))

    @Test
    fun testDatesNextMonthThirdMonth() = assert(testDates(T + 50, T + 70))

    @Test
    fun testDatesMonthLater() = assert(testDates(T + 15, T + 100))

    @Test
    fun testDatesThirdMonthLater() = assert(testDates(T + 88, T + 100))

    @Test
    fun testDatesLater() = assert(!testDates(T + 100, T + 200))


}