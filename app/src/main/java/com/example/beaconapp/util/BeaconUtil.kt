package com.example.beaconapp.util

object BeaconUtil {
    // iBeaconのフォーマット
    const val IBEACON_FORMAT: String = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
    // 探索対象のビーコンのUUID（複数のビーコンを検知する場合は設定しない）
    const val UUID: String = "00000000-017c-1001-b000-001c4db7979b"
    // 通知基準距離（メートル）
    const val DISTANCE_FOR_NOTICE = 0.5
}