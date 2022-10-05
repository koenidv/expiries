package de.koenidv.expiries

import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.WeekFields
import java.util.*

fun LocalDate.getWeekNumber(): Int {
    return this.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
}