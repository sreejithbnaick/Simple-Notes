package com.simplemobiletools.notes.pro.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.notes.pro.R
import com.simplemobiletools.notes.pro.extensions.initializeDB
import com.simplemobiletools.notes.pro.extensions.isDBInitialized
import com.simplemobiletools.notes.pro.extensions.showToast
import kotlinx.android.synthetic.main.activity_passphrase.*

class PassphraseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passphrase)
    }

    fun onPassPhrase(@Suppress("UNUSED_PARAMETER") view: View) {
        initializeDB(et_passphrase)
        if (isDBInitialized) {
            finishAffinity()
            MainActivity.startActivity(this)
        } else {
            showToast(getString(R.string.wrong_passphrase))
        }
    }

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, PassphraseActivity::class.java))
        }
    }
}
