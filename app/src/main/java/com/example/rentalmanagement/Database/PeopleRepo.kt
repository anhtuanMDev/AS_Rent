package com.example.rentalmanagement.Database

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Interfaces.PeopleDAO
import com.example.rentalmanagement.Models.EntityPeople
import com.example.rentalmanagement.Utils.KeyTagUtils
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

    fun deletePeople(houseID: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.deletePeopleByAddressID(houseID)
        }
    }

    fun deletePeople(roomID: List<Int>) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.deletePeopleByRoomID(roomID)
        }
    }

    fun updatePeople(data: EntityPeople) {
        CoroutineScope(Dispatchers.IO).launch {
            peopleDAO.insertPeople(data)
        }
    }

    fun checkPeopleInRoom(list: List<Int>, callBack: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val people = peopleDAO.checkPeopleInRoom(list)
            Log.d(KeyTagUtils.TAG_LOG, people.toString())
            callBack(people.isEmpty())
        }
    }

}