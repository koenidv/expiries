package de.koenidv.expiries

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Location(
    @PrimaryKey val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val icon_name: String,
    @ColumnInfo val comment: String,
)

@Dao
interface LocationDao {
    @Query("SELECT * FROM location")
    fun getAll(): List<Location>

    @Insert
    suspend fun insert(vararg locations: Location)
}
