package com.lasockiquenon.coopertest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.lasockiquenon.coopertest.utils.PrefsFragment as PreferenceFragmentCompat

class SettingsActivity : BaseThemedActivity() {

    var comeFromTestActivity : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        comeFromTestActivity = intent.getBooleanExtra("SendByTestActivity", false)
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
        if (comeFromTestActivity){
            if (isParametersComplete()) {
                val intent = Intent()
                setResult(1, intent)
                finish()
            } else {
                completeParameters()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun isParametersComplete(): Boolean {
        val mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        return (mSharedPreference.contains("birthday") && mSharedPreference.contains("name")
                && mSharedPreference.contains("gender"))

    }

    private fun completeParameters() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(R.string.completeAllParameters)
            .setCancelable(false)
            .setPositiveButton(R.string.acceptCompleteParameters, null)
            .setNegativeButton(R.string.returnToTheTest, DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent()
                setResult(0,intent)
                finish()
            })
            .show()

    }


}