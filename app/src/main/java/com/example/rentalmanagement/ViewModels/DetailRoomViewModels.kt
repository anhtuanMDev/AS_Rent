package com.example.rentalmanagement.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Database.AppDatabase
import com.example.rentalmanagement.Database.PeopleRepo
import com.example.rentalmanagement.Database.RoomRepo
import com.example.rentalmanagement.Models.EntityPeople

class DetailRoomViewModels(application: Application) : AndroidViewModel(application) {
    private val roomRepo: RoomRepo
    private val peopleRepo: PeopleRepo

    init {
        val roomDAO = AppDatabase.getDatabase(application).roomDao()
        val peopleDAO = AppDatabase.getDatabase(application).peopleDao()
        roomRepo = RoomRepo(roomDAO)
        peopleRepo = PeopleRepo(peopleDAO)
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

}