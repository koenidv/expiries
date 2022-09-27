package de.koenidv.expiries

import androidx.room.*
import java.time.LocalDate

@Entity
data class Article(
    @ColumnInfo val barcode: String?,
    @ColumnInfo val name: String?,
    @ColumnInfo val expiry: LocalDate?,
    @ColumnInfo val image_url: String?,
    @ColumnInfo val location_id: String?,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
)

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article")
    fun getAll(): List<Article>

    @Insert
    fun insert(vararg articles: Article)

    @Delete
    fun delete(article: Article)

    @Update
    fun update(article: Article)
}