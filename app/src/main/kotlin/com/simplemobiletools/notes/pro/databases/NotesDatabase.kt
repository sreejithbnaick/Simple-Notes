package com.simplemobiletools.notes.pro.databases

import android.content.Context
import android.util.Log
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

private const val DB_NAME = "notes-secure.db"

@Database(entities = [Note::class, Widget::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun NotesDao(): NotesDao

    abstract fun WidgetsDao(): WidgetsDao

    companion object {
        private var db: NotesDatabase? = null
        private var tempDB: NotesDatabase? = null

        fun getInstance(): NotesDatabase = db as NotesDatabase

        fun isInitialized() = db != null

        fun createInstance(context: Context, passphraseField: EditText): NotesDatabase {
            return db ?: createInstanceInternal(context, passphraseField)
        }

        private fun createInstanceInternal(context: Context, passphraseField: EditText): NotesDatabase {
            synchronized(NotesDatabase::class) {
                val factory = SafeHelperFactory.fromUser(passphraseField.text)

                return Room.databaseBuilder(context, NotesDatabase::class.java, DB_NAME)
                        .openHelperFactory(factory)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                insertFirstNote(context)
                            }
                        }).build().apply {
                            openHelper.setWriteAheadLoggingEnabled(true)
                            tempDB = this
                            if (isDbUnlocked(this)) db = this
                        }
            }
        }

        fun destroyInstance() {
            db = null
            tempDB = null
        }

        private fun insertFirstNote(context: Context) {
            Executors.newSingleThreadScheduledExecutor().execute {
                val generalNote = context.resources.getString(R.string.general_note)
                val note = Note(null, generalNote, "", TYPE_TEXT)
                tempDB?.apply {
                    NotesDao().insertOrUpdate(note)
                } ?: kotlin.run { Log.d("test", "No db") }
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
