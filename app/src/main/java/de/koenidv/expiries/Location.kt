package de.koenidv.expiries

import androidx.room.AutoMigration
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
@AutoMigration(from = 3, to = 4)
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val icon_name: String,
    @ColumnInfo val comment: String?,
)

@Dao
interface LocationDao {
    @Query("SELECT * FROM location ORDER BY name")
    fun observeAll(): Flow<List<Location>>

    @Query("SELECT * FROM location ORDER BY name")
    fun getAll(): List<Location>

    @Query("SELECT * FROM location WHERE id = :id")
    suspend fun get(id: Int): Location?


    @Insert
    suspend fun insert(vararg locations: Location)

    @Delete
    suspend fun delete(location: Location)
}
