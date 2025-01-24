package com.example.rentalmanagement.Database

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Models.EntityAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressRepo(private val addressDao: AddressDAO) {
    fun getHouseData(): LiveData<List<EntityAddress>> = addressDao.getAddress()

    fun insertNewHouse(data: EntityAddress, callBack: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            addressDao.insertAddress(data)
            callBack()
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

    fun getLastHouse(callBack: (EntityAddress) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = addressDao.getLastData()
            try {
                if (data != null) {
                    callBack(data)
                } else {
                    Log.d("error in Address Repo", data.toString())
                }
            } catch (e: NullPointerException) {
                Log.d("error in Address Repo", data.toString())
            }
        }.isCompleted
    }

}