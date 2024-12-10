package com.example.rentalmanagement.Models

data class AddressModel (
    var country: String,
    var city: String,
    var district: String,
    var ward: String,
    var street: String,
    var houseNumber: String,
    var imagePath: String,
    var departmentType: String,
    var room: Int
)