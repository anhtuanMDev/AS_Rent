package com.example.rentalmanagement.Utils

import android.net.Uri
import android.widget.Toast
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
                bind.btsEdtAddress.error = "Địa chỉ không được để trống"
            } else bind.btsEdtAddress.error = null
        }

        bind.btsEdtRooms.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val rooms = editable.toString().toInt()
                    when (bind.btsEdtApartmentType.text.toString()) {
                        apartmentTypes[0] -> {
                            if (rooms > 15 || rooms < 0) {
                                bind.btsEdtRooms.error = "Số phòng tối đa cho phòng trọ là 15"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }

                        apartmentTypes[1] -> {
                            if (rooms > 10) {
                                bind.btsEdtRooms.error = "Số phòng tối đa cho nhà là 10"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }

                        apartmentTypes[2] -> {
                            if (rooms < 10) {
                                bind.btsEdtRooms.error = "Số phòng tối thiểu cho chung cư là 10"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    bind.btsEdtRooms.error = "Vui lòng nhập số phòng hợp lệ"
                }
            } else {
                bind.btsEdtRooms.error = "Số phòng không được để trống"
            }
        }

        bind.btsEdtPrice.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val price = editable.toString().toInt()
                    if (price < 1000000) {
                        bind.btsEdtPrice.error = "Giá thuê tối thiểu là 1 triệu"
                    } else {
                        bind.btsEdtPrice.error = null
                    }
                } catch (e: NumberFormatException) {
                    bind.btsEdtPrice.error = "Vui lòng nhập giá thuê hợp lệ"
                }
            } else {
                bind.btsEdtPrice.error = "Giá thuê không được để trống"
            }
        }
    }

    fun checkErrorAddAddress(bind: BottomsheetAddAddressBinding): Boolean {
        if (bind.btsEdtAddress.error != null) {
            return false
        }
        if (bind.btsEdtRooms.error != null) {
            return false
        }
        if (bind.btsEdtPrice.error != null) {
            return false
        }
        return true
    }
}