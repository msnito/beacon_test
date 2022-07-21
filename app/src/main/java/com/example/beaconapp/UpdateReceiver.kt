package com.example.beaconapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import com.example.beaconapp.activity.TAG
import com.example.beaconapp.adapter.BeaconData
import com.example.beaconapp.util.BeaconUtil

/**
 * 受信時処理
 */
class UpdateReceiver : BroadcastReceiver() {

    private lateinit var notification:NotificationController

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.getStringArrayListExtra("data")
        val beaconDistance = extras?.get(5).toString()

        BeaconData(
            extras?.get(0).toString(),
            beaconDistance
        )

        val dataStore = context.getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val vibrationFlag = dataStore.getString("vibration", "off")

        // 特定の距離近づいたら処理を実行する
        if ((beaconDistance.toDouble() < BeaconUtil.DISTANCE_FOR_NOTICE) && vibrationFlag === "on") {
            // 初期化
            NotificationController.getInstance(context)
            // 音を鳴らす
            NotificationController.getInstance(context).playSound(NotificationController.SOUND_ALERM)

            Log.d(TAG, "Distance to the Beacon is within ${BeaconUtil.DISTANCE_FOR_NOTICE}m.")
        }

        Log.d(TAG, extras?.get(0).toString())
        Log.d(TAG, beaconDistance)
    }
}