package com.example.rentalmanagement.Models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rentalmanagement.Enums.DepartmentTypeEnum

@Entity
data class EntityAddress (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "ward") val ward: String,
    @ColumnInfo(name = "street") val street: String,
    @ColumnInfo(name = "house_number") val houseNumber: String,
    @ColumnInfo(name = "image_path") val imagePath: String,
    @ColumnInfo(name = "district") val district: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "departmentType") val departmentType: String,
    @ColumnInfo(name = "room") val room: Int,
)
