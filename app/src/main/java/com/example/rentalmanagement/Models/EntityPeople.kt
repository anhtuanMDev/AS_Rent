package com.example.rentalmanagement.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityPeople(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomID: Int,
    val name: String,
    val deposit: Int,
    val birth: String,
    val email: String,
    val gender: String,
    val identifyID: String,
    val phoneNumber: String,
    val roleInHouse: String,
    val validateDate: String, // Ngày đăng ký tạm trú
    val startRentDate: String, // Ngày bắt đầu thuê nhà
    val comingUpPayDate: String?, // Ngày đến hạn thanh toán
    val permanentAddress: String,
    val hasPayRent: Boolean = false,
)

data class FamilyMemberModel (
    val name: String,
    val gender: String,
    val birthday: String,
    val relationship: String,
    val identification: String,
)