package de.koenidv.expiries

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

enum class ArticleUnit {
    PIECE, GRAM, KILOGRAM, LITER, MILLILITER
}

@Entity
data class Article(
    @ColumnInfo val barcode: String?,
    @ColumnInfo val name: String?,
    @ColumnInfo val expiry: LocalDate?,
    @ColumnInfo val image_url: String?,
    @ColumnInfo val location_id: String?,
    @ColumnInfo val created_at: LocalDate?,
    @ColumnInfo val amount: Float?,
    @ColumnInfo val unit: String?,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) : ListItem(TYPE_ARTICLE)

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article ORDER BY expiry")
    fun getAllSorted(): Flow<List<Article>>

    @Insert
    suspend fun insert(vararg articles: Article)

    @Delete
    suspend fun delete(article: Article)

    @Update
    suspend fun update(article: Article)

    @Query("SELECT DISTINCT name FROM article ORDER BY created_at DESC LIMIT 1000")
    fun getSuggestedNames(): List<String>
}