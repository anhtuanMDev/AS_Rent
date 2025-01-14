package com.example.rentalmanagement.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.rentalmanagement.Database.AddressRepo
import com.example.rentalmanagement.Database.AppDatabase
import com.example.rentalmanagement.Database.RoomRepo
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.EntityRoom
import com.example.rentalmanagement.Models.RoomSmallDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseViewModels(application: Application) : AndroidViewModel(application) {
    val getData: LiveData<List<EntityAddress>>
    private val houseRepo: AddressRepo
    private val roomRepo: RoomRepo

    init {
        val houseDao = AppDatabase.getDatabase(application).addressDao()
        val repo = AddressRepo(houseDao)
        val roomDao = AppDatabase.getDatabase(application).roomDao()
        roomRepo = RoomRepo(roomDao)
        getData = repo.getHouseData()
        houseRepo = repo
    }

    fun addHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            houseRepo.insertNewHouse(data)
            houseRepo.getLastHouse(
                callBack = {
                    val roomArr = ArrayList<EntityRoom>()
                    for (i in 1..data.room) {
                        roomArr.add(EntityRoom(0, it.id, ""))
                    }
                    roomRepo.insertNewRoom(roomArr)
                }
            )

        }
    }

    fun getMinInfoRoom(houseID: Int): LiveData<List<RoomSmallDisplay>> {
        return roomRepo.getMinInfoRoom(houseID)
    }

    fun deleteHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            houseRepo.deleteHouse(data)
            roomRepo.deleteRoom(data.id)
        }
    }
}