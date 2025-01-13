package com.example.rentalmanagement.Database

import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Interfaces.RoomDAO
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.RoomSmallDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressRepo(private val addressDao: AddressDAO) {
    fun getHouseData(): LiveData<List<EntityAddress>> = addressDao.getAddress()

    fun insertNewHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch {
            addressDao.insertAddress(data)
        }
    }

    fun updateHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch {
            addressDao.updateAddress(data)
        }
    }

    fun deleteHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch {
            addressDao.delete(data)
        }
    }

    fun getDisplayRoom(houseID: Int): LiveData<List<RoomSmallDisplay>> {
        return addressDao.getMinInfoRoom(houseID)
    }

}