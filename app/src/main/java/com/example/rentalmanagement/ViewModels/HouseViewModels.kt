package com.example.rentalmanagement.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.rentalmanagement.Database.AddressRepo
import com.example.rentalmanagement.Database.AppDatabase
import com.example.rentalmanagement.Database.PeopleRepo
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
    private val peopleRepo: PeopleRepo

    init {
        val houseDao = AppDatabase.getDatabase(application).addressDao()
        val repo = AddressRepo(houseDao)
        val peopleDao = AppDatabase.getDatabase(application).peopleDao()
        val roomDao = AppDatabase.getDatabase(application).roomDao()
        peopleRepo = PeopleRepo(peopleDao)
        roomRepo = RoomRepo(roomDao)
        getData = repo.getHouseData()
        houseRepo = repo
    }

    fun addHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            houseRepo.insertNewHouse(data, callBack = {
                houseRepo.getLastHouse(
                    callBack = {
                        val roomArr = ArrayList<EntityRoom>()
                        for (i in 1..data.room) {
                            roomArr.add(EntityRoom(0, it.id, "$i"))
                        }
                        roomRepo.insertNewRoom(roomArr)
                    }
                )
            })
        }
    }

    fun addRoom(houseID: Int, index: Int) {
        roomRepo.insertNewRoom(EntityRoom(0, houseID, (index + 1).toString()))
        houseRepo.updateRoomNumber(houseID, index + 1)
    }

    fun addRoom(houseID: Int, index: Int, list: List<EntityRoom>) {
        roomRepo.insertNewRoom(list)
        houseRepo.updateRoomNumber(houseID, index + list.size)

    }

    fun getMinInfoRoom(houseID: Int): LiveData<List<RoomSmallDisplay>> {
        return roomRepo.getMinInfoRoom(houseID)
    }

    fun updateHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            houseRepo.updateHouse(data)
        }
    }

    fun deleteHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            peopleRepo.deletePeople(data.id)
            roomRepo.deleteRoom(data.id)
            houseRepo.deleteHouse(data)
        }
    }

    fun checkPeopleInRoom(roomID: List<Int>, callBack: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            peopleRepo.checkPeopleInRoom(roomID, {
                if (it) {
                    callBack(true)
                } else {
                    callBack(false)
                }
            })
        }
    }

    fun deleteRoom(data: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            peopleRepo.deletePeople(data)
            roomRepo.deleteRoom(data)
        }

    }
}