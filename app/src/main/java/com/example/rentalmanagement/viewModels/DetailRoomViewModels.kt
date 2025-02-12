package com.example.rentalmanagement.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.rentalmanagement.database.AppDatabase
import com.example.rentalmanagement.database.PeopleRepo
import com.example.rentalmanagement.database.RoomRepo
import com.example.rentalmanagement.models.EntityPeople
import com.example.rentalmanagement.models.RoomSmallDisplay

class DetailRoomViewModels(application: Application) : AndroidViewModel(application) {
    private val roomRepo: RoomRepo
    private val peopleRepo: PeopleRepo

    init {
        val roomDAO = AppDatabase.getDatabase(application).roomDao()
        val peopleDAO = AppDatabase.getDatabase(application).peopleDao()
        roomRepo = RoomRepo(roomDAO)
        peopleRepo = PeopleRepo(peopleDAO)
    }

//    suspend fun getNeighbours(houseID: Int, owner: LifecycleOwner): List<RoomSmallDisplay> {
//        return viewModelScope.async(Dispatchers.IO) {
//            val list = mutableListOf<RoomSmallDisplay>()
//            roomRepo.getMinInfoRoom(houseID).observe(owner) {
//                list.addAll(it)
//            }
//            return@async list
//        }.await()
//    }

    fun getNeighbours(houseID: Int): LiveData<List<RoomSmallDisplay>> {
        return roomRepo.getMinInfoRoom(houseID)
    }

    fun getData(roomID: Int): LiveData<List<EntityPeople>> {
        return peopleRepo.getData(roomID)
    }

    fun addData(data: EntityPeople) {
        peopleRepo.insertNewPeople(data)
    }

    fun addData(data: List<EntityPeople>) {
        peopleRepo.insertNewPeople(data)
    }

    fun deleteRoom(roomID: Int) {
        peopleRepo.deletePeople(roomID)
        roomRepo.deleteRoom(roomID)
    }

}