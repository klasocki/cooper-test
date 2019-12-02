package com.example.coopertest

import android.content.Context
import android.media.MediaPlayer

class AudioNotifier(private val context: Context) {
    private var mediaPlayer = MediaPlayer.create(context, R.raw.left_10)

    fun notifyAboutTimeLeft(milisUntilFinished: Long) {
        val milisMod5sec = milisUntilFinished.toInt() % (5 * 1000)
        val maxError = 50
        // We check if should notify only for multiples of 5sec.
        // milisUntilFinished is not accurate, 50 error rate works well
        if (milisMod5sec <= maxError || milisMod5sec >= 5 * 1000 - maxError) {
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