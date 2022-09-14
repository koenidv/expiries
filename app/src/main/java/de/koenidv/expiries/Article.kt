package de.koenidv.expiries

import androidx.room.*
import java.util.*

@Entity
data class Article(
    @PrimaryKey val id: String,
    @ColumnInfo val barcode: String,
    @ColumnInfo var name: String,
    @ColumnInfo var expiry: Date,
    @ColumnInfo val image_url: String,
    @ColumnInfo var location_id: String,
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