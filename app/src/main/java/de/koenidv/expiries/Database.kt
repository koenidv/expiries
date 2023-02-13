package de.koenidv.expiries

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.room.*
import androidx.room.Database
import org.threeten.bp.LocalDate
import java.io.File
import java.nio.file.Files.copy
import java.nio.file.StandardCopyOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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

        fun close() {
            if (this::db.isInitialized) {
                db.close()
            }
        }
    }

}

fun shareDbFile(context: Context) {
    val db = context.getDatabasePath("database").absoluteFile
    // Close the database to make sure everything is written to disk
    de.koenidv.expiries.Database.close()

    val date = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date())
    val external = File(context.getExternalFilesDir(null), "backup-${date}.exp")
    copy(db.toPath(), external.toPath(), StandardCopyOption.REPLACE_EXISTING)

    val uri = FileProvider.getUriForFile(context, "de.koenidv.expiries.fileprovider", external)

    val shareIntent: Intent = Intent().apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "expiries/backup"
    }
    startActivity(context, Intent.createChooser(shareIntent, null), null)
    // Recreate because database references are now invalid
    // todo There must be a better way
    Toast.makeText(context, R.string.settings_backup_toast, Toast.LENGTH_SHORT).show()
    MainActivity().recreate()

    // fixme Permission denial because URI permission not granted, but works for now
}

fun launchRestoreFilePicker(activity: Activity) {
    var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
    chooseFile.type = "*/*"
    chooseFile = Intent.createChooser(chooseFile, "Choose a file")
    startActivityForResult(activity, chooseFile, SettingsActivity.REQUEST_RESTORE, null)
}

fun restoreDbFile(context: Context, uri: Uri) {
    de.koenidv.expiries.Database.close()
    val db = context.getDatabasePath("database").absoluteFile

    copy(
        context.contentResolver.openInputStream(uri),
        db.toPath(),
        StandardCopyOption.REPLACE_EXISTING
    )

    Toast.makeText(context, R.string.settings_restore_toast, Toast.LENGTH_SHORT).show()
    MainActivity().recreate()
}

class Converters {
    @TypeConverter
    fun toDate(dateLong: Long?): LocalDate? = dateLong?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromDate(date: LocalDate?): Long? = date?.toEpochDay()
}