package de.koenidv.expiries

import android.content.Context
import androidx.room.*
import androidx.room.Database
import org.threeten.bp.LocalDate

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
    fun toDate(dateLong: Long?): LocalDate? = dateLong?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromDate(date: LocalDate?): Long? = date?.toEpochDay()
}