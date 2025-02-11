package com.example.rentalmanagement.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.models.FamilyMemberModel
import com.example.rentalmanagement.databinding.HolderFamilyPeopleBinding

class FamilyMemberAdapter(holdData: (FamilyMemberModel) -> Unit) : RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var dataset: List<FamilyMemberModel> = emptyList()
    private val setData = holdData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HolderFamilyPeopleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataset[position]
        val binding = HolderFamilyPeopleBinding.bind(holder.itemView)
        holder.itemView.setOnClickListener {
            setData(data)
        }
        binding.holderFamilyPeopleName.text = data.name
        binding.holderFamilyPeopleRelationship.text = data.relationship

    }

    fun addData(data: FamilyMemberModel) {
        dataset = dataset + data
        notifyItemInserted(dataset.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<FamilyMemberModel>) {
        dataset = data
        notifyDataSetChanged()
    }

    fun updateData(data: FamilyMemberModel, position: Int) {
        dataset = dataset.mapIndexed { index, item ->
            if (index == position) data else item
        }
        notifyItemChanged(position)
    }

    fun deleteData(position: Int) {
        dataset = dataset.filterIndexed { index, _ -> index != position }
        notifyItemRemoved(position)
    }

}