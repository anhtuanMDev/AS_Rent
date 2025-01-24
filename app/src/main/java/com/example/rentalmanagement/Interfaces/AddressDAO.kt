package com.example.rentalmanagement.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.EntityRoom

@Dao
interface AddressDAO {

    @Insert
    suspend fun insertAddress(address: EntityAddress)

    @Update
    suspend fun updateAddress(address: EntityAddress)

    @Query("SELECT * FROM entityaddress")
    fun getAddress(): LiveData<List<EntityAddress>>

    @Query("SELECT * FROM entityroom")
    suspend fun getDataAddress(): List<EntityRoom>

    @Query("SELECT * FROM entityaddress ORDER BY id DESC LIMIT 1")
    suspend fun getLastData(): EntityAddress?

    @Delete
    suspend fun delete(address: EntityAddress)
}