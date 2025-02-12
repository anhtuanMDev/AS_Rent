package com.example.rentalmanagement.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rentalmanagement.interfaces.AddressDAO
import com.example.rentalmanagement.interfaces.PeopleDAO
import com.example.rentalmanagement.interfaces.RoomDAO
import com.example.rentalmanagement.models.EntityAddress
import com.example.rentalmanagement.models.EntityPeople
import com.example.rentalmanagement.models.EntityRoom

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