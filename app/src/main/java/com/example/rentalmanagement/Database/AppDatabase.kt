package com.example.rentalmanagement.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Interfaces.PeopleDAO
import com.example.rentalmanagement.Interfaces.RoomDAO
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.EntityPeople
import com.example.rentalmanagement.Models.EntityRoom

@Database(entities = [EntityAddress::class, EntityRoom::class, EntityPeople::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun addressDao(): AddressDAO
    abstract fun roomDao(): RoomDAO
    abstract fun peopleDao(): PeopleDAO


    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null){
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}