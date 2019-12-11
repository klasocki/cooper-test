package com.lasockiquenon.coopertest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.lasockiquenon.coopertest.utils.PrefsFragment as PreferenceFragmentCompat

class SettingsActivity : BaseThemedActivity() {

    var cameFromTestActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cameFromTestActivity = intent.getBooleanExtra("SentByTestActivity", false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            !cameFromTestActivity -> super.onBackPressed()
            cameFromTestActivity && !isSettingsComplete() -> confirmLeavingWithoutFinishing()
            else -> {
                val intent = Intent()
                setResult(1, intent)
                finish()
            }
        }
    }

    private fun isSettingsComplete(): Boolean {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        return (mSharedPreference.contains("birthday") && mSharedPreference.contains("name")
                && mSharedPreference.contains("gender"))
    }

    private fun confirmLeavingWithoutFinishing() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(R.string.completeAllParameters)
            .setCancelable(false)
            .setPositiveButton(R.string.acceptCompleteParameters, null)
            .setNegativeButton(R.string.returnToTheTest) { _: DialogInterface, _: Int ->
                val intent = Intent()
                setResult(0, intent)
                finish()
            }
            .show()
    }


}