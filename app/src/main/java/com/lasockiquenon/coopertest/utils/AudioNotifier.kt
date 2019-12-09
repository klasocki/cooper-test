package com.lasockiquenon.coopertest.utils

import android.content.Context
import android.media.MediaPlayer
import com.lasockiquenon.coopertest.R

class AudioNotifier(private val context: Context) {
    private var mediaPlayer = MediaPlayer.create(context,
        R.raw.left_10
    )

    fun notifyAboutTimeLeft(milisUntilFinished: Long) {
        val milisMod5sec = milisUntilFinished.toInt() % (5 * 1000)
        val maxError = 60
        // We check if should notify only for multiples of 5sec.
        // milisUntilFinished could be anywhere within tickInterval - 100ms, so we have an errorRate of 60 (2*60 > 100)
        if (milisMod5sec >= maxError && milisMod5sec <= 5 * 1000 - maxError) {
            return
        }
        val secondsUntilFinished = (milisUntilFinished / 1000).toInt()
        var fileToPlay = -1
        for (timeLeft in listOf(10, 15, 30, 60, 120, 240, 360, 480, 600)) {
            if (secondsUntilFinished in timeLeft - 1..timeLeft) {
                fileToPlay = context.resources.getIdentifier("left_$timeLeft", "raw", context.packageName)
            }
        }
        if (fileToPlay != -1) {
            playNewFile(fileToPlay)
        }
    }

    fun playTestStartFile() {
        playNewFile(R.raw.left_10)
    }

    fun stop() {
        mediaPlayer.release()
    }

    private fun playNewFile(file: Int) {
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(context, file)
        mediaPlayer.start()
    }
}