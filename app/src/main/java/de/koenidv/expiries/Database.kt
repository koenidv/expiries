package de.koenidv.expiries

import android.content.Context
import android.provider.ContactsContract.Data
import androidx.room.*
import androidx.room.Database
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@Database(entities = [Article::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        lateinit var db: de.koenidv.expiries.Database
        fun get(context: Context): de.koenidv.expiries.Database {
            if (!this::db.isInitialized) {
                db = Room.databaseBuilder(
                    context,
                    de.koenidv.expiries.Database::class.java,
                    "database"
                ).build()
            }

            return db
        }
    }
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