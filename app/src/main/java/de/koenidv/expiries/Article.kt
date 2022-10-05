package de.koenidv.expiries

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

@Entity
data class Article(
    @ColumnInfo val barcode: String?,
    @ColumnInfo val name: String?,
    @ColumnInfo val expiry: LocalDate?,
    @ColumnInfo val image_url: String?,
    @ColumnInfo val location_id: String?,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) {
    companion object {
        fun addListDividers(list: List<Article>): List<ListItem> {
            val result = ArrayList<ListItem>()
            list.forEachIndexed { index, article ->
                if (article.expiry != null && (index == 0 || article.expiry != list[index - 1].expiry)) {
                    result.add(ListItem(article.expiry))
                }
                result.add(ListItem(article))
            }
            return result.toList()
        }

    }
}

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
}