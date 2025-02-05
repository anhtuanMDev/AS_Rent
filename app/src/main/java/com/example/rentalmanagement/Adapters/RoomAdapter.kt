package com.example.rentalmanagement.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Models.RoomSmallDisplay
import com.example.rentalmanagement.R
import com.example.rentalmanagement.Screen.DetailRoomActivity
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.HOUSE_ID
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.ROOM_ID
import com.example.rentalmanagement.ViewModels.HouseViewModels

class RoomAdapter(private val houseID: Int, private val houseVM: HouseViewModels) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    var dataset: List<RoomSmallDisplay> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.min_room_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.holder_txt_room)
        if (position == dataset.size) {
            textView.text = "+"
            holder.itemView.setOnClickListener {
                houseVM.addRoom(houseID, position)
            }
        } else {
            val room = dataset[position]
            room.name.also { textView.text = it }
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailRoomActivity::class.java)
                intent.putExtra(ROOM_ID, room.id)
                intent.putExtra(HOUSE_ID, houseID)
                holder.itemView.context.startActivity(intent)
            }
        }
    }

    fun updateData(newDataset: List<RoomSmallDisplay>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

}