package com.example.beaconapp

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

const val MSG_ACTIVITY_TO_SERVICE = 1
const val MSG_SERVICE_TO_ACTIVITY = 2
const val TAG: String = "MainActivityDebug"

class MainActivity : AppCompatActivity(), ServiceConnection {

    private val PERMISSION_REQUEST_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2

    private var upReceiver: UpdateReceiver? = null
    private var intentFilter: IntentFilter? = null
    var uuidText: TextView? = null
    var distanceText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        uuidText= findViewById(R.id.uuid);
        distanceText = findViewById(R.id.distance)

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

        

        // サービス起動
        startService(Intent(this, BeaconService::class.java))

        upReceiver = UpdateReceiver()
        intentFilter = IntentFilter()
        intentFilter!!.addAction("DO_ACTION")
        registerReceiver(upReceiver, intentFilter)


        val beacon1 = BeaconData("dummy1", "0")
        val beacon2 = BeaconData("dummy2", "1")
        val beacons = arrayOf(beacon1, beacon2)

        // use a linear layout manager
        recycler_view.layoutManager = LinearLayoutManager(this)

        // set adapter
        recycler_view.adapter = CustomAdapter(beacons)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler_view.setHasFixedSize(true)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }

    //サービスをバインドすると実行されるイベント
    override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
        //サービスからMessengerのインスタンスが返されるのでメンバ変数として保存しておく
//        mServiceMessenger = Messenger(service)
//        bound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
//        mServiceMessenger = null
//        bound = false
    }

    // サービスから値を受け取ったら動かしたい内容を書く
    private class UpdateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val extras = intent.getStringArrayListExtra("data")
            BeaconData(extras?.get(0).toString(), extras?.get(5).toString())
            Toast.makeText(context, extras?.get(5).toString(),  Toast.LENGTH_LONG).show()
            Log.d(TAG, extras?.get(0).toString())
            Log.d(TAG, extras?.get(5).toString())
        }
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