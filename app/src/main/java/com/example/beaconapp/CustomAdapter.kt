package com.example.beaconapp

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_item.view.*

data class BeaconData(
    val uuid: String,
    val distance: String
)

class CustomAdapter(private val beaconList: Array<BeaconData>): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val uuid: TextView
        val distance: TextView

        init {
            uuid = view.findViewById(R.id.uuid)
            distance = view.findViewById(R.id.distance)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int = beaconList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val beacon = beaconList[position]

        holder.uuid.text = beacon.uuid
        holder.distance.text = beacon.distance

    }
}