package com.example.rentalmanagement.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityAddress (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "electricity") val electricity: Int,
    @ColumnInfo(name = "water") val water: Int,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "departmentType") val departmentType: String,
    @ColumnInfo(name = "room") val room: Int,
)
