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

class RoomAdapter(val dataset: List<RoomSmallDisplay>): RecyclerView.Adapter<RoomAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.min_room_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.holder_txt_room)
        val room = dataset[position]
        "${position + 1}".also { textView.text = it }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailRoomActivity::class.java)
            intent.putExtra("id", room.id)
            holder.itemView.context.startActivity(intent)
        }
    }
}