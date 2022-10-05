package de.koenidv.expiries

import org.junit.Test
import org.threeten.bp.LocalDate

class DetermineAddArticleListDividersTest {

    private val T: Long = 19269L
    private val dividers = ArticleListDividers()

    // Using epochDays to store dates: Tue, Oct 4, 2022 is epochDay T = 19269
    private fun testDates(day1: Long, day2: Long): Boolean {
        val today = LocalDate.ofEpochDay(T)
        val date1 = LocalDate.ofEpochDay(day1)
        val date2 = LocalDate.ofEpochDay(day2)
        return dividers.determineAddDividers(date1, date2, today)
    }

    @Test
    fun testSame() = assert(!testDates(T, T))

    @Test
    fun testSameTomorrow() = assert(!testDates(T + 1, T + 1))

    @Test
    fun testDatesBefore() = assert(!testDates(T - 12, T - 7))

    @Test
    fun testDatesBeforeRecent() = assert(testDates(T - 7, T - 6))

    @Test
    fun testDatesRecent() = assert(!testDates(T - 6, T - 1))

    @Test
    fun testDatesRecentAfter() = assert(testDates(T - 2, T))

    @Test
    fun testDatesTodayTomorrow() = assert(testDates(T, T + 1))

    @Test
    fun testDatesTomorrowWeek() = assert(testDates(T + 1, T + 5))

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
    fun testDatesMonthNextMonth() = assert(testDates(T + 28, T + 58))

    @Test
    fun testDatesNextMonthLater() = assert(testDates(T + 50, T + 70))

    @Test
    fun testDatesMonthLater() = assert(testDates(T + 15, T + 100))

    @Test
    fun testDatesLater() = assert(!testDates(T + 100, T + 200))

}

class ResolveArticleListDividersTest {

    private val T: Long = 19269L
    private val dividers = ArticleListDividers()

    // Using epochDays to store dates: Tue, Oct 4, 2022 is epochDay T = 19269
    private fun testDates(day1: Long): Int {
        val today = LocalDate.ofEpochDay(T)
        val date = LocalDate.ofEpochDay(day1)
        return dividers.resolveDividerDate(date, today)
    }

    @Test
    fun testBefore() = assert(testDates(T - 7) == R.string.timeframe_expired)

    @Test
    fun testRecently() = assert(testDates(T - 1) == R.string.timeframe_expired_recently)

    @Test
    fun testToday() = assert(testDates(T) == R.string.timeframe_today)

    @Test
    fun testTomorrow() = assert(testDates(T + 1) == R.string.timeframe_tomorrow)

    @Test
    fun testWeek() = assert(testDates(T + 2) == R.string.timeframe_this_week)

    @Test
    fun testNextWeek() = assert(testDates(T + 7) == R.string.timeframe_next_week)

    @Test
    fun testMonth() = assert(testDates(T + 15) == R.string.timeframe_this_month)

    @Test
    fun testNextMonth() = assert(testDates(T + 28) == R.string.timeframe_next_month)

    @Test
    fun testLater() = assert(testDates(T + 58) == R.string.timeframe_later)

}