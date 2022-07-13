package com.example.beaconapp.util

import android.content.Context
import android.widget.Toast

class ToastUtil (private val mContext: Context, var mText: String) :
    Runnable {
    override fun run() {
        Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show()
    }
}