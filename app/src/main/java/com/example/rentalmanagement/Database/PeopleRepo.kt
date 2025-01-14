package com.example.rentalmanagement.Database

import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Interfaces.PeopleDAO
import com.example.rentalmanagement.Models.EntityPeople
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleRepo(private val peopleDAO: PeopleDAO) {
    fun getData(roomID: Int): LiveData<List<EntityPeople>> = peopleDAO.getPeopleByRoomID(roomID)

    fun insertNewPeople(data: EntityPeople) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.insertPeople(data)
        }
    }

    fun insertNewPeople(data: List<EntityPeople>) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.insertPeople(data)
        }
    }

    fun deletePeople(data: EntityPeople) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.deletePeople(data)
        }
    }

    fun deletePeople(data: List<EntityPeople>) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.deletePeople(data)
        }
    }

    fun updatePeople(data: EntityPeople) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.insertPeople(data)
        }
    }

}