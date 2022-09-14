package de.koenidv.expiries

import androidx.room.*
import androidx.room.Database
import java.util.*

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}

class Converters {
    @TypeConverter
    fun toDate(dateLong: Long?): Date? = dateLong?.let { Date(it) }

    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time
}