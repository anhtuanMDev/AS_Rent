package com.example.rentalmanagement.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.R

class HouseAdapter(private var dataset: List<EntityAddress>) :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {

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
    }

    // Method to update the data dynamically
    fun updateData(newDataset: List<EntityAddress>) {
        dataset = newDataset
        notifyDataSetChanged()
    }
}
