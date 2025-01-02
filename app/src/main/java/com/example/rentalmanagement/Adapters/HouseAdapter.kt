package com.example.rentalmanagement.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.R

class HouseAdapter() :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    private var dataset: List<EntityAddress> = emptyList()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = dataset[position]
        holder.itemView.findViewById<TextView>(R.id.house_holder_address).text = address.address
    }

    // Method to update the data dynamically
    fun updateData(newDataset: List<EntityAddress>) {
        dataset = newDataset
        notifyDataSetChanged()
    }
}
