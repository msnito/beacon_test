package com.example.beaconapp

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.beaconapp.activity.TAG

/**
 * 端末の通知を制御する
 * https://101010.fun/programming/android-sound-pool-effect.html
 */
class NotificationController (context: Context) {
    private var soundPool: SoundPool? = null

    companion object {

        var SOUND_ALERM = 0

        var INSTANCE:NotificationController? = null
        fun getInstance(context: Context) =
            INSTANCE ?: NotificationController(context).also {
                INSTANCE = it
            }
    }

    init {
        createSoundPool()
        loadSoundIds(context)
    }

    private fun createSoundPool() {
        /**
         * SDKのバージョンによって処理を変える
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadSoundPool()
        } else {
            loadOldSoundPool()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun loadSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            // USAGE_MEDIA
            // USAGE_GAME
            .setUsage(AudioAttributes.USAGE_GAME)
            // CONTENT_TYPE_MUSIC
            // CONTENT_TYPE_SPEECH, etc.
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            // ストリーム数に応じて
            .setMaxStreams(2)
            .build()
    }

    private fun loadOldSoundPool() {
        soundPool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
    }

    private fun loadSoundIds(context: Context){
        soundPool?.let {
            println("サウンドファイルロード")
            SOUND_ALERM = it.load(context, R.raw.alerm, 1)
        }
    }

    fun playSound(soundId:Int){
        soundPool?.let {
            it.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            Log.d("SoundDebug","再生")
        }
    }

    fun close(){
        soundPool?.release()
        soundPool = null
    }
}