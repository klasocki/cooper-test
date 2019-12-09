package com.lasockiquenon.coopertest

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.content_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        if (Build.VERSION.SDK_INT >= 24) {
            testInfoView.text = Html.fromHtml(getString(R.string.info_text), FROM_HTML_MODE_LEGACY)
        } else {
            testInfoView.text = Html.fromHtml(getString(R.string.info_text))
        }
    }

}
