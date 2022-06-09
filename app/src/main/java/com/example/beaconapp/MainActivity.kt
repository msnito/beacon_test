package com.example.beaconapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.altbeacon.beacon.*

// https://qiita.com/kenmaeda51415/items/ac5a2d5a15783bbe9192

class MainActivity : AppCompatActivity(), RangeNotifier, MonitorNotifier {
    private lateinit var mBeaconManager: BeaconManager
    private val TAG: String = "BeaconDebug"

    var uuid = Identifier.parse(BeaconUtil.UUID)

    private val mRegion = Region("unique-id-001", uuid, null, null)

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // デバイスのBLE対応チェック
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // 未対応の場合、Toast表示
            Toast.makeText(applicationContext,"このデバイスはBLE未対応です", Toast.LENGTH_LONG).show()
        }

        // API 23以上かのチェック
        if (Build.VERSION.SDK_INT >= 23) {
            // パーミッションの要求
            checkPermission()
        }

        // ビーコンマネージャーのインスタンス作成
        mBeaconManager = BeaconManager.getInstanceForApplication(this)
        // iBeaconの受信設定：iBeaconのフォーマットを登録する
        mBeaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconUtil.IBEACON_FORMAT))
        mBeaconManager.addMonitorNotifier(this)
        mBeaconManager.addRangeNotifier(this)
        mBeaconManager.startMonitoring(mRegion)
        mBeaconManager.startRangingBeacons(mRegion)
    }

    override fun onPause() {
        super.onPause()
        mBeaconManager.stopMonitoring(mRegion)
        mBeaconManager.stopRangingBeacons(mRegion)
        Log.d(TAG, "ビーコンサービス解除")
    }

    override fun onResume() {
        super.onResume()
        mBeaconManager.addMonitorNotifier(this)
        mBeaconManager.addRangeNotifier(this)
        mBeaconManager.startMonitoring(mRegion)
        mBeaconManager.startRangingBeacons(mRegion)
        Log.d(TAG, "ビーコンサービス起動")
    }

    override fun didEnterRegion(region: Region?) {
        //領域への入場を検知
        Log.d(TAG, "Enter Region ${region?.uniqueId}")
    }

    override fun didExitRegion(region: Region?) {
        //領域からの退場を検知
        Log.d(TAG, "Exit Region ${region?.uniqueId}")
    }

    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        // 検知したBeaconの情報
        Log.d(TAG, "beacons.size ${beacons?.size}")
        beacons?.let {
            for (beacon in beacons) {
                Log.d(TAG, "UUID: ${beacon.id1}, major: ${beacon.id2}, minor: ${beacon.id3}, RSSI: ${beacon.rssi}, TxPower: ${beacon.txPower}, Distance: ${beacon.distance}")
            }
        }
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
        //領域への入退場のステータス変化を検知（INSIDE: 1, OUTSIDE: 0）
        Log.d(TAG, "Determine State: $state")
    }

    // パーミッションの許可チェック
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        // パーミッション未許可の時
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可ダイアログの表示
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }
    }
}