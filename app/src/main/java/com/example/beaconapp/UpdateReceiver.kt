package com.example.beaconapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.beaconapp.activity.TAG
import com.example.beaconapp.adapter.BeaconData
import com.example.beaconapp.util.BeaconUtil

// サービスから値を受け取ったら動かしたい内容を書く
class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val extras = intent.getStringArrayListExtra("data")

        val beaconDistance = extras?.get(5).toString()

        BeaconData(
            extras?.get(0).toString(),
            beaconDistance
        )

        val vc = VibrationController(context)

        // 特定の距離離れたら端末を振動させる
        if (beaconDistance.toDouble() > BeaconUtil.DISTANCE_FOR_NOTICE) {
            vc.start()
            Log.d(TAG, "distance was over 5m.")
        }

        Toast.makeText(context, beaconDistance,  Toast.LENGTH_LONG).show()
        Log.d(TAG, extras?.get(0).toString())
        Log.d(TAG, beaconDistance)
    }
}