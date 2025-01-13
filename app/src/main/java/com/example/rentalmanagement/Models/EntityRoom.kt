package com.example.rentalmanagement.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityRoom (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val houseID: Int,
    val name: String = "$id",
    val hasPayRent: Boolean = false, // Đã thanh toán tiền nhà
)

data class RoomSmallDisplay (
    val id: Int,
    val name: String
)