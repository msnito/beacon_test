package com.example.beaconapp

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * 端末の振動を制御する
 */
class VibrationController (context: Context?) {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun start() {
        /**
         * SDKのバージョンによって処理を変える
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(300,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(300)
        }
    }
}