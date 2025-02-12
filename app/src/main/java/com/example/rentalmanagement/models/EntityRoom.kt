package com.example.rentalmanagement.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityRoom (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val houseID: Int,
    val name: String = "$id",
)

data class RoomSmallDisplay (
    val id: Int,
    val name: String
)