package com.example.rentalmanagement.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.rentalmanagement.Interfaces.AddressDAO
import com.example.rentalmanagement.Models.EntityAddress

@Database(entities = [EntityAddress::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun addressDao(): AddressDAO

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