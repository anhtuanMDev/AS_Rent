package com.example.rentalmanagement.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.rentalmanagement.Database.AppDatabase
import com.example.rentalmanagement.Database.PeopleRepo
import com.example.rentalmanagement.Models.EntityPeople

class CreateRegisterFormViewModels(application: Application) : AndroidViewModel(application) {
    private val peopleRepo: PeopleRepo

    init {
        val ppDAO = AppDatabase.getDatabase(application).peopleDao()
        peopleRepo = PeopleRepo(ppDAO)
    }

    fun getInfo(roomID: Int): LiveData<List<EntityPeople>> {
        return peopleRepo.getData(roomID)
    }

}