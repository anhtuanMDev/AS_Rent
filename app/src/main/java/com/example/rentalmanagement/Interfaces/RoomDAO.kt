package com.example.rentalmanagement.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rentalmanagement.Models.EntityRoom
import com.example.rentalmanagement.Models.RoomSmallDisplay

@Dao
interface RoomDAO {
    @Insert
    suspend fun insertRoom(room: EntityRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(data: List<EntityRoom>)

    @Update
    suspend fun updateRoom(newRoom: EntityRoom)

    @Query("SELECT * FROM entityroom WHERE houseID = :houseID")
    fun getAllRoom(houseID: Int): LiveData<List<EntityRoom>>

    @Query("SELECT id, name FROM entityroom WHERE houseID = :houseID")
    fun getMinInfoRoom (houseID: Int) : LiveData<List<RoomSmallDisplay>>

    @Query("DELETE FROM entityroom WHERE houseID = :houseID")
    suspend fun deleteRoom(houseID: Int)

    @Delete
    suspend fun deleteRoom(room: EntityRoom)
}