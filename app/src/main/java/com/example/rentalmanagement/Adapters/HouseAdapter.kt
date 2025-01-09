package com.example.rentalmanagement.Adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.R
import com.example.rentalmanagement.Utils.ValidateUtils
import com.example.rentalmanagement.databinding.BottomsheetDetailHouseBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class HouseAdapter() :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    private var dataset: List<EntityAddress> = emptyList()
    private val validate = ValidateUtils()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_holder, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = dataset[position]
        val context = holder.itemView.context
        holder.itemView.findViewById<CardView>(R.id.house_holder_notify).visibility = View.GONE
        val uri: Uri = Uri.parse(address.imagePath)
        if (validate.isLocalFileExists(uri)){
            holder.itemView.findViewById<ImageView>(R.id.house_holder_image).setImageURI(uri)
        }

        val apartmentTypes = holder.itemView.context.resources.getStringArray(R.array.apartment_type)

        when (address.departmentType) {
            apartmentTypes[0] -> { // DÃY TRỌ
                holder.itemView.findViewById<ImageView>(R.id.house_holder_type)
                    .setImageResource(R.drawable.board_house)
            }
            apartmentTypes[1] -> { // CHUNG CƯ
                holder.itemView.findViewById<ImageView>(R.id.house_holder_type)
                    .setImageResource(R.drawable.apartment)
            }
            apartmentTypes[2] -> { // NHÀ CHO THUÊ
                holder.itemView.findViewById<ImageView>(R.id.house_holder_type)
                    .setImageResource(R.drawable.house)
            }
            else -> {
                // Default case (if needed)
                holder.itemView.findViewById<ImageView>(R.id.house_holder_type)
                    .setImageResource(R.drawable.board_house)
            }
        }

        holder.itemView.setOnClickListener{
            val dialog = BottomSheetDialog(context)
            val dialogBinding = BottomsheetDetailHouseBinding.inflate(LayoutInflater.from(context))
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialogBinding.btsDetailHeader.text = address.departmentType
            dialogBinding.btsDetailAddress.text = address.address
            val sb = StringBuilder(dialogBinding.btsDetailRooms.text)
            sb.append(" Tổng số phòng:  ",  address.room)
            dialogBinding.btsDetailRooms.text = sb
            val adapter = RoomAdapter(List(address.room){""})
            dialogBinding.btsDetailRoomsDisplay.layoutManager = GridLayoutManager(holder.itemView.context, 4, GridLayoutManager.VERTICAL, false)

            dialogBinding.btsDetailRoomsDisplay.adapter = adapter
            if (validate.isLocalFileExists(uri)){
                dialogBinding.btsDetailImg.setImageURI(uri)
            }
            dialog.setContentView(dialogBinding.root)
            dialog.show()
        }
        }

    // Method to update the data dynamically
    fun updateData(newDataset: List<EntityAddress>) {
        dataset = newDataset
        notifyDataSetChanged()
    }
}
