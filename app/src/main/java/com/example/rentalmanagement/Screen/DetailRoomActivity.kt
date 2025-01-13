package com.example.rentalmanagement.Screen

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rentalmanagement.Adapters.DetailRoomFragmentAdapter
import com.example.rentalmanagement.Models.EntityPeople
import com.example.rentalmanagement.R
import com.example.rentalmanagement.databinding.ActivityDetailRoomBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class DetailRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityDetailRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        val peopleList = listOf(
            EntityPeople(
                name = "John Doe",
                birth = "01/01/1990",
                email = "john.doe@example.com",
                gender = "Male",
                identifyID = "123456789",
                phoneNumber = "1234567890",
                roleInHouse = "Tenant",
                validateDate = "12/12/2024",
                startRentDate = "12/12/2024",
                comingUpPayDate = "12/12/2024",
                deposit = 145000,
                id = 0,
                permanentAddress = "Something"
            ),
            EntityPeople(
                name = "Jane Smith",
                birth = "02/02/1995",
                email = "jane.smith@example.com",
                gender = "Female",
                identifyID = "987654321",
                phoneNumber = "0987654321",
                roleInHouse = "Owner",
                validateDate = "12/12/2024",
                startRentDate = "12/12/2024",
                comingUpPayDate = "12/12/2024",
                deposit = 144000,
                id = 1,
                permanentAddress = "Something"
            )
        )

        val adapter = DetailRoomFragmentAdapter(this, peopleList)
        binding.detailRoomViewpager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.detailRoomViewpager)
    }
}