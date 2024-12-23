package com.example.rentalmanagement.Database

import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Models.EntityAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddressRepo(private val addressDao: AddressDAO) {
    fun insertNewHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch{
            addressDao.insertAddress(data)
        }
    }

    fun getAllHouse(): List<EntityAddress> {
        return addressDao.getAddress()
    }

    fun updateHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch{
            addressDao.updateAddress(data)
        }
    }

    fun deleteHouse(data: EntityAddress) {
        CoroutineScope(Dispatchers.IO).launch{
            addressDao.delete(data)
        }
    }
}