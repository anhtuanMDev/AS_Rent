package com.example.rentalmanagement.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.rentalmanagement.Database.AddressRepo
import com.example.rentalmanagement.Database.AppDatabase
import com.example.rentalmanagement.Models.EntityAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HouseViewModels(application: Application): AndroidViewModel(application) {
    val getData: LiveData<List<EntityAddress>>
    private val houseRepo: AddressRepo

    init {
        val houseDao = AppDatabase.getDatabase(application).addressDao()
        val repo = AddressRepo(houseDao)
        getData = repo.getHouseData
        houseRepo = repo
    }

    fun addHouse(data: EntityAddress) {
        viewModelScope.launch(Dispatchers.IO) {
            houseRepo.insertNewHouse(data)
        }
    }
}