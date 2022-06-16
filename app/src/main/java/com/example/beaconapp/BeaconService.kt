package com.example.beaconapp

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import org.altbeacon.beacon.*
import java.lang.ref.WeakReference

// https://qiita.com/kenmaeda51415/items/ac5a2d5a15783bbe9192

class BeaconService : Service(), RangeNotifier, MonitorNotifier{

    private var mServiceMessenger: Messenger? = null
    private lateinit var mBeaconManager: BeaconManager
    private val TAG: String = "BeaconServiceDebug"
    var uuid = Identifier.parse(BeaconUtil.UUID)
    private val mRegion = Region("unique-id-001", uuid, null, null)

    internal class ServiceHandler(
        service: BeaconService
    ) : Handler(Looper.getMainLooper()) {
        private val mService = WeakReference<BeaconService>(service)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_ACTIVITY_TO_SERVICE ->{
                    try {
                        val msg2 = Message.obtain(null, MSG_SERVICE_TO_ACTIVITY, 0, 0)
                        msg.replyTo.send(msg2)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        mServiceMessenger = Messenger(ServiceHandler(this))
        return mServiceMessenger!!.binder
    }

    override fun onCreate(){
        super.onCreate()
        // ビーコンマネージャーのインスタンス作成
        mBeaconManager = BeaconManager.getInstanceForApplication(this)
        // iBeaconの受信設定：iBeaconのフォーマットを登録する
        mBeaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconUtil.IBEACON_FORMAT))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mBeaconManager.addMonitorNotifier(this)
        mBeaconManager.addRangeNotifier(this)
        mBeaconManager.startMonitoring(mRegion)
        mBeaconManager.startRangingBeacons(mRegion)
        Log.d(TAG, "ビーコンサービス起動")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mBeaconManager.stopMonitoring(mRegion)
        mBeaconManager.stopRangingBeacons(mRegion)
        Log.d(TAG, "ビーコンサービス解除")
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
                sendBeaconData("${beacon.id1}", "${beacon.id2}", "${beacon.id3}", "${beacon.rssi}", "${beacon.txPower}", "${beacon.distance}")
                Log.d(TAG, "UUID: ${beacon.id1}, major: ${beacon.id2}, minor: ${beacon.id3}, RSSI: ${beacon.rssi}, TxPower: ${beacon.txPower}, Distance: ${beacon.distance}")
            }
        }
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
        //領域への入退場のステータス変化を検知（INSIDE: 1, OUTSIDE: 0）
        Log.d(TAG, "Determine State: $state")
    }

    private fun sendBeaconData(
        uuid: String,
        major: String,
        minor: String,
        rssi: String,
        txPower: String,
        distance: String){
        val data: ArrayList<String> = arrayListOf(
            uuid, major, minor, rssi, txPower, distance
        )
        val broadcast = Intent()
        broadcast.putStringArrayListExtra("data", data)
        broadcast.action = "DO_ACTION"
        baseContext.sendBroadcast(broadcast)
    }
}