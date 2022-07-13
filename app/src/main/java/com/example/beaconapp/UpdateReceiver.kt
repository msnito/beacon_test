package com.example.beaconapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.beaconapp.activity.TAG
import com.example.beaconapp.adapter.BeaconData
import com.example.beaconapp.util.BeaconUtil

/**
 * 受信時処理
 */
class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.getStringArrayListExtra("data")

        val beaconDistance = extras?.get(5).toString()

        BeaconData(
            extras?.get(0).toString(),
            beaconDistance
        )

        val dataStore = context.getSharedPreferences("DataStore", Context.MODE_PRIVATE)

        val vibrationFlag = dataStore.getString("vibration", "off")
        val vc = VibrationController(context)

//        val mToast = Toast.makeText(context, beaconDistance, Toast.LENGTH_LONG)

        // 特定の距離近づいたら端末を振動させる
        if ((beaconDistance.toDouble() < BeaconUtil.DISTANCE_FOR_NOTICE) && vibrationFlag === "on") {
            vc.start()
//            mToast.show()
            Log.d(TAG, "Distance to the Beacon is within 3m.")
        }

        Log.d(TAG, extras?.get(0).toString())
        Log.d(TAG, beaconDistance)
    }
}