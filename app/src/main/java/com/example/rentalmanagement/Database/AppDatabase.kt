package com.example.rentalmanagement.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Models.EntityAddress

@Database(entities = [EntityAddress::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun addressDao(): AddressDAO
}