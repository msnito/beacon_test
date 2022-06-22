package com.example.beaconapp

import kotlin.math.pow

class BeaconCalcurator {
    fun calDistance (txPower: Int, rssi: Int): Double {
        return 10.0.pow((txPower - rssi) / 20.0)
    }
}