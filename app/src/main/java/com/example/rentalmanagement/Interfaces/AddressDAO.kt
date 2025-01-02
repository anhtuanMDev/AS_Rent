package com.example.rentalmanagement.Interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.rentalmanagement.Models.EntityAddress

@Dao
interface AddressDAO {

    @Insert
    suspend fun insertAddress(address: EntityAddress)

    @Update
    suspend fun updateAddress(address: EntityAddress)

    @Query("SELECT * FROM entityaddress")
    fun getAddress(): LiveData<List<EntityAddress>>

    @Delete
    suspend fun delete(address: EntityAddress)
}