package com.example.rentalmanagement.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.models.EntityAddress
import com.example.rentalmanagement.models.EntityRoom
import com.example.rentalmanagement.models.RoomSmallDisplay
import com.example.rentalmanagement.R
import com.example.rentalmanagement.utils.CamerasUtils
import com.example.rentalmanagement.utils.KeyTagUtils
import com.example.rentalmanagement.utils.NumberUtils
import com.example.rentalmanagement.utils.ValidateUtils
import com.example.rentalmanagement.viewModels.HouseViewModels
import com.example.rentalmanagement.databinding.BottomsheetAddAddressBinding
import com.example.rentalmanagement.databinding.BottomsheetDetailHouseBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HouseAdapter(
    private val houseViewModel: HouseViewModels,
    pickImageLauncher: ActivityResultLauncher<String>,
    private val lifecycleOwner: LifecycleOwner,
    requestPermissionsLauncher: ActivityResultLauncher<Array<String>>,
    private val apartmentTypes: List<String>
) :
    RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    private var dataset: List<EntityAddress> = emptyList()
    private val validate = ValidateUtils()
    private val cameraUtils: CamerasUtils = CamerasUtils(
        context = houseViewModel.getApplication(),
        lifecycleOwner = lifecycleOwner,
        pickImageLauncher = pickImageLauncher,
        requestPermissionsLauncher = requestPermissionsLauncher
    )

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
            dialogBinding.btsDetailRooms.setText("Tổng số phòng : ${address.room}")

            val adapter = RoomAdapter(address.id, houseViewModel)

            houseViewModel.getMinInfoRoom(address.id).observeForever { rooms ->
                if (rooms.size > 44) {
                    val params = dialogBinding.btsDetailRoomsDisplay.layoutParams
                    params.height = NumberUtils.dpToPx(450, context)
                    dialogBinding.btsDetailRoomsDisplay.layoutParams = params
                    dialogBinding.btsDetailRooms.setText("Tổng số phòng : ${rooms.size}")
                } else {
                    val params = dialogBinding.btsDetailRoomsDisplay.layoutParams
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    dialogBinding.btsDetailRoomsDisplay.layoutParams = params
                    dialogBinding.btsDetailRooms.setText("Tổng số phòng : ${rooms.size}")
                }

                adapter.updateData(rooms)
            }

            dialogBinding.btsDetailAddressEdit.setOnClickListener {
                createEditBottomSheet(context, address, adapter.dataset)
                dialog.dismiss()
            }

            dialogBinding.btsDetailAddressAddRoom.setOnClickListener {
                createDialogAddRoom(holder.itemView.context, address)
            }

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
            }, {})
            return@setOnLongClickListener true
        }
    }

    private fun alertDeleteDialog(context: Context, onConfirm: () -> Unit, onCancel: () -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bạn muốn xóa nơi này ?")
        builder.setMessage("Bấm có để xác nhận và bấm không để hủy thao tác")
        builder.setPositiveButton("Có") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        builder.setNegativeButton("Không") { dialog, _ ->
            onCancel()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun createEditBottomSheet(context: Context, address: EntityAddress, roomList: List<RoomSmallDisplay>) {
        val dialog = BottomSheetDialog(context)
        val dialogBinding = BottomsheetAddAddressBinding.inflate(LayoutInflater.from(context))
        dialogBinding.btsEdtRooms.setText(with(address) { room.toString() })
        dialogBinding.btsEdtAddress.setText(address.address)
        dialogBinding.btsEdtPrice.setText(with(address) { price.toString() })
        dialogBinding.btsEdtApartmentType.setText(address.departmentType)
        dialogBinding.btsBtnFinish.setText(R.string.form_update)

        val rawPath = address.imagePath.replace("file://", "")
        val imgPath = File(rawPath)

        cameraUtils.imageView = dialogBinding.btsImg

        if (imgPath.exists()) {
            dialogBinding.btsImg.setImageURI(Uri.fromFile(imgPath))
        }
        dialogBinding.btsImg.setOnClickListener {
            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                cameraUtils.requestImagePermission()
            }
        }


        validate.setErrorAddress(dialogBinding, apartmentTypes)

        dialogBinding.btsBtnFinish.setOnClickListener {
            if (!validate.checkErrorAddAddress(dialogBinding)) {
                return@setOnClickListener
            }
            val newAddress = EntityAddress(
                address.id,
                cameraUtils.fileUri.toString(),
                dialogBinding.btsEdtAddress.text.toString(),
                dialogBinding.btsEdtPrice.text.toString().toInt(),
                dialogBinding.btsEdtApartmentType.text.toString(),
                dialogBinding.btsEdtRooms.text.toString().toInt(),
                0,
                "",
                0.0
            )

            if (address.room != newAddress.room) {
                val rooms = address.room - newAddress.room
                if (rooms > 0) {
                    val roomLists = (newAddress.room..<address.room).map { roomList[it].id }
                    Log.d(KeyTagUtils.TAG_LOG, roomLists.toString())
                    houseViewModel.checkPeopleInRoom(roomLists, {
                        if (!it) {
                            lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                withContext(Dispatchers.Main) {
                                    val builder = AlertDialog.Builder(context)
                                    builder.setTitle("Bạn có muốn xóa không")
                                    builder.setMessage("Một trong những phòng sẽ bị xóa do sự thay đổi có chứa thông tin nhạy cảm, bạn vẫn muốn tiếp tục chứ ?")
                                    builder.setPositiveButton("Có") { dialog, _ ->
                                        houseViewModel.deleteRoom(roomLists)
                                        houseViewModel.updateHouse(newAddress)
                                        dialog.dismiss()
                                    }
                                    builder.setNegativeButton("Không") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    builder.create().show()
                                }
                            }
                        } else {
                            houseViewModel.deleteRoom(roomLists)
                            houseViewModel.updateHouse(newAddress)
                        }
                    })
                } else if (rooms < 0) {
                    val addList = (1..Math.abs(rooms)).map { roomNumber ->
                        EntityRoom(
                            id = 0,
                            houseID = address.id,
                            name = "${address.room + roomNumber}"
                        )
                    }
                    houseViewModel.addRoom(address.id, address.room, addList)
                    houseViewModel.updateHouse(newAddress)
                }
            }
            dialog.dismiss()

        }

        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }

    // Method to update the data dynamically
    fun updateData(newDataset: List<EntityAddress>) {
        dataset = newDataset
        notifyDataSetChanged()
    }

    private fun createDialogAddRoom(context: Context, address: EntityAddress) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Thêm phòng")

        // Create an EditText
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_NUMBER // Set input type to number
        editText.textSize = 18f // Set text size (in sp)
        editText.hint = "Nhập số phòng muốn thêm"

        // Calculate margin in pixels
        val margin = NumberUtils.dpToPx(16, context)

        // Create a container for the EditText
        val container = FrameLayout(context)
        // Set padding on the container (this effectively creates a margin for the child view)
        container.setPadding(margin, margin, margin, margin)
        container.addView(editText)

        // Set the container as the view of the dialog
        builder.setView(container)

        // Set the positive button action
        builder.setPositiveButton("Thêm") { _, _ ->
            val length = editText.text.toString()
            val roomList = (1..length.toInt()).map { roomNumber ->
                EntityRoom(
                    id = 0,
                    houseID = address.id,
                    name = "${address.room + roomNumber}"
                )
            }
            houseViewModel.addRoom(address.id, address.room, roomList)
        }

        // Set the negative button action
        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }

        // Create and show the dialog
        val dialog = builder.create()
        dialog.show()
    }


}
