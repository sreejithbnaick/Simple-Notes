package com.simplemobiletools.notes.pro.extensions

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.EditText
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.databases.NotesDatabase
import com.simplemobiletools.notes.pro.helpers.*
import com.simplemobiletools.notes.pro.interfaces.NotesDao
import com.simplemobiletools.notes.pro.interfaces.WidgetsDao

val Context.config: Config get() = Config.newInstance(applicationContext)

val Context.notesDB: NotesDao get() = NotesDatabase.getInstance().NotesDao()

val Context.widgetsDB: WidgetsDao get() = NotesDatabase.getInstance().WidgetsDao()

val Context.isDBInitialized: Boolean get() = NotesDatabase.isInitialized()

fun Context.initializeDB(passphraseField: EditText) = NotesDatabase.createInstance(applicationContext, passphraseField)

fun Context.getTextSize() = when (config.fontSize) {
    FONT_SIZE_SMALL -> resources.getDimension(R.dimen.smaller_text_size)
    FONT_SIZE_LARGE -> resources.getDimension(R.dimen.big_text_size)
    FONT_SIZE_EXTRA_LARGE -> resources.getDimension(R.dimen.extra_big_text_size)
    else -> resources.getDimension(R.dimen.bigger_text_size)
}

fun Context.updateWidgets() {
    val widgetIDs = AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(ComponentName(applicationContext, MyWidgetProvider::class.java))
    if (widgetIDs.isNotEmpty()) {
        Intent(applicationContext, MyWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIDs)
            sendBroadcast(this)
        }
    }
}
