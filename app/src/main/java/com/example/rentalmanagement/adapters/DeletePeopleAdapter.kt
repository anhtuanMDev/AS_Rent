package com.example.rentalmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.models.SelectPeople
import com.example.rentalmanagement.R
import com.example.rentalmanagement.databinding.ItemDeletePersonBinding

class DeletePeopleAdapter(peopleList: List<SelectPeople> = emptyList(), private val checkAll: AppCompatCheckBox) :
    RecyclerView.Adapter<DeletePeopleAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var dataset: List<SelectPeople> = peopleList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_delete_person, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun updateData(newDataset: List<SelectPeople>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    fun checkAll(): Boolean {
        if (dataset.all { it.select }) {
            dataset.forEach { it.select = false }
            notifyDataSetChanged()
            return false
        } else {
            dataset.forEach { it.select = true }
            notifyDataSetChanged()
            return true
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val people = dataset[position]
        val binding = ItemDeletePersonBinding.bind(holder.itemView)
        binding.itemDeletePersonTxtName.text = people.people.name
        binding.itemDeletePersonTxtRole.text = people.people.roleInHouse
        binding.itemDeletePersonCheckBox.isChecked = people.select

        holder.itemView.setOnClickListener {
            dataset[position].select = !dataset[position].select
            if (dataset.all { it.select }) {
                checkAll.isChecked = true
            } else {
                checkAll.isChecked = false
            }
            notifyItemChanged(position)
        }
    }
}