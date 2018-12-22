package com.simplemobiletools.notes.pro.databases

import android.content.Context
import android.widget.EditText
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.helpers.TYPE_TEXT
import com.simplemobiletools.notes.pro.interfaces.NotesDao
import com.simplemobiletools.notes.pro.interfaces.WidgetsDao
import com.simplemobiletools.notes.pro.models.Note
import com.simplemobiletools.notes.pro.models.Widget
import java.util.concurrent.Executors

private const val DB_NAME = "notes.db"

@Database(entities = [Note::class, Widget::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun NotesDao(): NotesDao

    abstract fun WidgetsDao(): WidgetsDao

    companion object {
        private var db: NotesDatabase? = null

        fun getInstance(): NotesDatabase = db as NotesDatabase

        fun isInitialized() = db != null

        fun createInstance(context: Context, passphraseField: EditText): NotesDatabase {
            return db ?: createInstanceInternal(context, passphraseField)
        }

        private fun createInstanceInternal(context: Context, passphraseField: EditText): NotesDatabase {
            synchronized(NotesDatabase::class) {
                val factory = SafeHelperFactory.fromUser(passphraseField.text)
                val dbLocal = Room.databaseBuilder(context, NotesDatabase::class.java, DB_NAME)
                        .openHelperFactory(factory)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                insertFirstNote(context)
                            }
                        }).build().apply {
                            openHelper.setWriteAheadLoggingEnabled(true)
                        }
                if (isDbUnlocked(dbLocal)) db = dbLocal
                return dbLocal
            }
        }

        fun destroyInstance() {
            db = null
        }

        private fun insertFirstNote(context: Context) {
            db?.apply {
                Executors.newSingleThreadScheduledExecutor().execute {
                    val generalNote = context.resources.getString(R.string.general_note)
                    val note = Note(null, generalNote, "", TYPE_TEXT)
                    NotesDao().insertOrUpdate(note)
                }
            }
        }

        private fun isDbUnlocked(db: NotesDatabase): Boolean {
            try {
                db.query("select count(*) from sqlite_master", null)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }
    }
}
