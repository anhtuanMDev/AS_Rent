package com.example.rentalmanagement.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.rentalmanagement.Models.EntityPeople

@Dao
interface PeopleDAO {
    @Insert
    suspend fun insertPeople(people: EntityPeople)

    @Insert
    suspend fun insertPeople(people: List<EntityPeople>)

    @Query("SELECT * FROM entitypeople WHERE roomID = :id")
    fun getPeopleByRoomID(id: Int): LiveData<List<EntityPeople>>

    @Query("DELETE FROM entitypeople WHERE roomID = :roomID")
    suspend fun deletePeople(roomID: Int)

    @Delete
    suspend fun deletePeople(people: EntityPeople)

    @Delete
    suspend fun deletePeople(people: List<EntityPeople>)

}