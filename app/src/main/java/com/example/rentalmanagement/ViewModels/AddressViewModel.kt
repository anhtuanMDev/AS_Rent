package com.example.rentalmanagement.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentalmanagement.Models.AddressModel

data class SharedAddress(
    var country: String = "",
    var city: String = "",
    var district: String = "",
    var ward: String = "",
    var street: String = "",
    var houseNumber: String = "",
    var imagePath: String = "",
    var departmentType: String = "",
    var room: Int = 0
)


class AddressViewModel : ViewModel() {
    private val _address = MutableLiveData(SharedAddress())
    val address: LiveData<SharedAddress> get() = _address

    fun updateAddressField(field: String, value: String) {
        _address.value?.let { currentAddress ->
            when (field) {
                "street" -> currentAddress.street = value
                "houseNumber" -> currentAddress.houseNumber = value
                "city" -> currentAddress.city = value
                "district" -> currentAddress.district = value
                "ward" -> currentAddress.ward = value
                "country" -> currentAddress.country = value
                "departmentType" -> currentAddress.departmentType = value
                "room" -> currentAddress.room = value.toIntOrNull() ?: 0
                "imagePath" -> currentAddress.imagePath = value
            }
            _address.value = currentAddress // Update LiveData
        }
    }
}