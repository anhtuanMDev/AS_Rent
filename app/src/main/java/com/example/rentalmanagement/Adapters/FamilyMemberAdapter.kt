package com.example.rentalmanagement.Adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Models.FamilyMemberModel
import com.example.rentalmanagement.databinding.DialogDetailFamilyMemberBinding
import com.example.rentalmanagement.databinding.HolderFamilyPeopleBinding

class FamilyMemberAdapter() : RecyclerView.Adapter<FamilyMemberAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    var dataset: List<FamilyMemberModel> = emptyList()
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
            createDetailDialog(data, holder.itemView.context)
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

    fun deleteData(position: Int) {
        dataset = dataset.filterIndexed { index, _ -> index != position }
        notifyItemRemoved(position)
    }


    private fun createDetailDialog(info: FamilyMemberModel, context: Context) {
        val builder = AlertDialog.Builder(context)
        val bind = DialogDetailFamilyMemberBinding.inflate(LayoutInflater.from(context))
        builder.setTitle(info.name)
        bind.holderDetailFamilyMemberEdtId.setText(info.identification)
        bind.holderDetailFamilyMemberEdtBirth.setText(info.birthday)
        builder.setView(bind.root)
        val dialog = builder.create()
        dialog.show()
    }
}