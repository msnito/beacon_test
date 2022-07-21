package com.example.beaconapp.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beaconapp.NotificationController
import com.example.beaconapp.R
import com.example.beaconapp.UpdateReceiver
import com.example.beaconapp.adapter.BeaconData
import com.example.beaconapp.adapter.CustomAdapter
import com.example.beaconapp.service.BeaconService
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

        checkLocationPermission()

        val toggle: ToggleButton = findViewById(R.id.toggle_button)
        val dataStore = getSharedPreferences("DataStore", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = dataStore.edit()

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Key: input, value: text
                editor.putString("vibration", "on")
                editor.apply()
            } else {
                // Key: input, value: text
                editor.putString("vibration", "off")
                editor.apply()
            }
        }


        val beacon1 = BeaconData("dummy1", "0")
        val beacon2 = BeaconData("dummy2", "1")
        val beacons = arrayOf(beacon1, beacon2)

        // use a linear layout manager
//        recycler_view.layoutManager = LinearLayoutManager(this)

        // set adapter
//        recycler_view.adapter = CustomAdapter(beacons)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        recycler_view.setHasFixedSize(true)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        intent = Intent(this, BeaconService::class.java)
        startService(intent)

        upReceiver = UpdateReceiver()
        intentFilter = IntentFilter()
        intentFilter!!.addAction("DO_ACTION")
        registerReceiver(upReceiver, intentFilter)
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

    // パーミッションの許可チェック
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        // パーミッション未許可の時
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションの許可ダイアログの表示
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
        }
    }

    private fun checkLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("This app needs background location access")
                        builder.setMessage("Please grant location access so this app can detect beacons in the background.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener(DialogInterface.OnDismissListener {
                            requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                PERMISSION_REQUEST_BACKGROUND_LOCATION
                            )
                        })
                        builder.show()
                    } else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        builder.setTitle("Functionality limited")
                        builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.")
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.setOnDismissListener(DialogInterface.OnDismissListener { })
                        builder.show()
                    }
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        PERMISSION_REQUEST_FINE_LOCATION
                    )
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener(DialogInterface.OnDismissListener { })
                    builder.show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted")
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
            PERMISSION_REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted")
                } else {
                    val builder =
                        AlertDialog.Builder(this)
                    builder.setTitle("Functionality limited")
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setOnDismissListener { }
                    builder.show()
                }
                return
            }
        }
    }
}