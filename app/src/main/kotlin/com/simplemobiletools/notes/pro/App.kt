package com.simplemobiletools.notes.pro

import android.app.Application
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : Application() {

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        checkUseEnglish()
    }
}
