package de.koenidv.expiries

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

class Converters {
    @TypeConverter
    fun toDate(dateLong: Long?): LocalDate? =
        dateLong?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }

    @TypeConverter
    fun fromDate(date: LocalDate?): Long? =
        date?.let {
            ZonedDateTime.of(it.atStartOfDay(), ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
}