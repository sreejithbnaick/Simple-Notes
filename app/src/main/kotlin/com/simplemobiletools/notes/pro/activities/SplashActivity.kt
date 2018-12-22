package com.simplemobiletools.notes.pro.activities

import android.content.Intent
import com.simplemobiletools.commons.activities.BaseSplashActivity
import com.simplemobiletools.notes.pro.extensions.isDBInitialized
import com.simplemobiletools.notes.pro.helpers.OPEN_NOTE_ID

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        if (intent.extras?.containsKey(OPEN_NOTE_ID) == true) {
            Intent(this, MainActivity::class.java).apply {
                putExtra(OPEN_NOTE_ID, intent.getLongExtra(OPEN_NOTE_ID, -1L))
                startActivity(this)
            }
        } else if (!isDBInitialized) {
            PassphraseActivity.startActivity(this)
        } else {
            MainActivity.startActivity(this)
        }
        finish()
    }
}
