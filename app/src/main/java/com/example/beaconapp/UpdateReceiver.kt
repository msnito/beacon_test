package com.example.beaconapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

// サービスから値を受け取ったら動かしたい内容を書く
class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val extras = intent.getStringArrayListExtra("data")

        val beaconDistance = extras?.get(5).toString()

        BeaconData(extras?.get(0).toString(), beaconDistance)

        val bc = BeaconCalcurator()

        val newDistance = bc.calDistance(extras?.get(4)!!.toInt(), extras?.get(3)!!.toInt())

        val vc = VibrationController(context)
//        vc.start()

        if (beaconDistance.toDouble() > 3) {
            vc.start()
            Log.d(TAG, "distance was over 5m.")
        }

        Toast.makeText(context, beaconDistance,  Toast.LENGTH_LONG).show()
        Log.d(TAG, extras?.get(0).toString())
        Log.d("newdistance", newDistance.toString())
        Log.d(TAG, extras?.get(0).toString())
        Log.d(TAG, beaconDistance)
    }
}