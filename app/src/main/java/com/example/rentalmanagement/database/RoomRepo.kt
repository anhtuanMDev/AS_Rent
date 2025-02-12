package com.example.rentalmanagement.database

import androidx.lifecycle.LiveData
import com.example.rentalmanagement.interfaces.RoomDAO
import com.example.rentalmanagement.models.EntityRoom
import com.example.rentalmanagement.models.RoomSmallDisplay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomRepo(private val roomDao: RoomDAO) {
    fun getRoomData(houseID: Int): LiveData<List<EntityRoom>> = roomDao.getAllRoom(houseID)
    fun getMinInfoRoom(roomID: Int): LiveData<List<RoomSmallDisplay>> = roomDao.getMinInfoRoom(roomID)

    fun insertNewRoom(data: EntityRoom) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.insertRoom(data)
        }
    }

    fun insertNewRoom(data: List<EntityRoom>) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.insertRoom(data)
        }
    }

    fun updateRoom(data: EntityRoom) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.updateRoom(data)
        }
    }

    fun deleteRoom(data: EntityRoom) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.deleteRoom(data)
        }
    }

    fun deleteRoom(data: List<Int>) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.deleteRoom(data)
        }
    }

    fun deleteRoom(id: Int) {
        CoroutineScope(Dispatchers.IO).launch{
            roomDao.deleteRoom(id)
        }
    }

}