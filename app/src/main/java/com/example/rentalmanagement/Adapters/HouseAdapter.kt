package com.example.rentalmanagement.Adapters

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.util.TypedValueCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.RoomSmallDisplay
import com.example.rentalmanagement.R
import com.example.rentalmanagement.Utils.ValidateUtils
import com.example.rentalmanagement.ViewModels.HouseViewModels
import com.example.rentalmanagement.databinding.BottomsheetDetailHouseBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class HouseAdapter(private val houseViewModel: HouseViewModels) :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    private var dataset: List<EntityAddress> = emptyList()
    private val validate = ValidateUtils()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.address_holder, parent, false)
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
        if (validate.isLocalFileExists(uri)) {
            holder.itemView.findViewById<ImageView>(R.id.house_holder_image).setImageURI(uri)
        }

        val apartmentTypes =
            holder.itemView.context.resources.getStringArray(R.array.apartment_type)

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

        holder.itemView.setOnClickListener {
            val dialog = BottomSheetDialog(context)
            val dialogBinding = BottomsheetDetailHouseBinding.inflate(LayoutInflater.from(context))
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialogBinding.btsDetailHeader.text = address.departmentType
            dialogBinding.btsDetailAddress.text = address.address
            val sb = StringBuilder(dialogBinding.btsDetailRooms.text)
            sb.append(" Tổng số phòng:  ", address.room)
            dialogBinding.btsDetailRooms.text = sb

            val adapter = RoomAdapter()

            houseViewModel.getMinInfoRoom(address.id).observeForever { rooms ->
                if (rooms.size > 44) {
                    val params = dialogBinding.btsDetailRoomsDisplay.layoutParams
                    params.height = dpToPx(450, context)
                    dialogBinding.btsDetailRoomsDisplay.layoutParams = params
                } else {
                    val params = dialogBinding.btsDetailRoomsDisplay.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    dialogBinding.btsDetailRoomsDisplay.layoutParams = params
                }

                adapter.updateData(rooms)
            }

//            houseViewModel.getDisplayRoom(address.id).observeForever { rooms ->
//                adapter.updateData(rooms)
//            }
//            val adapter = RoomAdapter(if (roomData.isNotEmpty()) roomData[position] else emptyList())
            dialogBinding.btsDetailRoomsDisplay.layoutManager =
                GridLayoutManager(holder.itemView.context, 4, GridLayoutManager.VERTICAL, false)

            dialogBinding.btsDetailRoomsDisplay.adapter = adapter
            if (validate.isLocalFileExists(uri)) {
                dialogBinding.btsDetailImg.setImageURI(uri)
            }
            dialog.setContentView(dialogBinding.root)
            dialog.show()
        }

        holder.itemView.setOnLongClickListener {
            alertDeleteDialog(holder.itemView.context, {
                houseViewModel.deleteHouse(address)
            }, {
                Log.d("Delete House", "Cancel")
            })
            return@setOnLongClickListener true
        }
    }

    private fun alertDeleteDialog(context: Context, onConfirm: () -> Unit, onCancel: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bạn muốn xóa nơi này ?")
        builder.setMessage("Bấm có để xác nhận và bấm không để hủy thao tác")
        builder.setPositiveButton("Có") { dialog, which ->
            onConfirm()
            dialog.dismiss()
        }
        builder.setNegativeButton("Không") { dialog, which ->
            onCancel()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    // Method to update the data dynamically
    fun updateData(newDataset: List<EntityAddress>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

}
