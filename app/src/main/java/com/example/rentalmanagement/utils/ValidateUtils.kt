package com.example.rentalmanagement.utils

import android.net.Uri
import androidx.core.widget.addTextChangedListener
import com.example.rentalmanagement.databinding.BottomsheetAddAddressBinding
import java.io.File

class ValidateUtils {
    fun isLocalFileExists(uri: Uri): Boolean {
        return try {
            val file = File(uri.path ?: return false)
            file.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun setErrorAddress(bind: BottomsheetAddAddressBinding, apartmentTypes: List<String>) {

        bind.btsEdtAddress.addTextChangedListener {
            if (it.toString().isEmpty()) {
                bind.btsLetAddress.error = "Địa chỉ không được để trống"
            } else bind.btsLetAddress.error = null
        }

        bind.btsEdtRooms.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val rooms = editable.toString().toInt()
                    when (bind.btsEdtApartmentType.text.toString()) {
                        apartmentTypes[0] -> {
                            if (rooms > 15 || rooms < 0) {
                                bind.btsLetRooms.error = "Số phòng tối đa cho phòng trọ là 15"
                            } else {
                                bind.btsLetRooms.error = null
                            }
                        }

                        apartmentTypes[1] -> {
                            if (rooms > 10) {
                                bind.btsLetRooms.error = "Số phòng tối đa cho nhà là 10"
                            } else {
                                bind.btsLetRooms.error = null
                            }
                        }

                        apartmentTypes[2] -> {
                            if (rooms < 10) {
                                bind.btsLetRooms.error = "Số phòng tối thiểu cho chung cư là 10"
                            } else {
                                bind.btsLetRooms.error = null
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    bind.btsLetRooms.error = "Vui lòng nhập số phòng hợp lệ"
                }
            } else {
                bind.btsLetRooms.error = "Số phòng không được để trống"
            }
        }

        bind.btsEdtPrice.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val price = editable.toString().toInt()
                    if (price < 1000000) {
                        bind.btsLetPrice.error = "Giá thuê tối thiểu là 1 triệu"
                    } else {
                        bind.btsLetPrice.error = null
                    }
                } catch (e: NumberFormatException) {
                    bind.btsLetPrice.error = "Vui lòng nhập giá thuê hợp lệ"
                }
            } else {
                bind.btsLetPrice.error = "Giá thuê không được để trống"
            }
        }

        bind.btsEdtApartmentType.addTextChangedListener {
            if (it.toString().isEmpty()) {
                bind.btsLetApartmentType.error = "Vui lòng chọn 1 kiểu nhà cho thuê"
            } else if (it.toString() !in apartmentTypes) {
                bind.btsLetApartmentType.error = "Không hỗ trợ kiểu nhà cho thuê này"
            } else {
                bind.btsLetApartmentType.error = null
            }
        }
    }

    fun checkErrorAddAddress(bind: BottomsheetAddAddressBinding): Boolean {
        val address = bind.btsEdtAddress.text.toString()
        val rooms = bind.btsEdtRooms.text.toString()
        val price = bind.btsEdtPrice.text.toString()
        val apartmentType = bind.btsEdtApartmentType.text.toString()

        if (address.isEmpty()) {
            bind.btsLetAddress.error = "Địa chỉ không được để trống"
        }

        if (rooms.isEmpty()) {
            bind.btsLetRooms.error = "Số phòng không được để trống"
        }

        if (price.isEmpty()) {
            bind.btsLetPrice.error = "Giá thuê không được để trống"
        }

        if (apartmentType.isEmpty()) {
            bind.btsLetApartmentType.error = "Vui lòng chọn 1 kiểu nhà cho thuê"
        }

        if (address.isEmpty() || rooms.isEmpty() || price.isEmpty() || apartmentType.isEmpty()) {
            return false
        }

        return true
    }
}