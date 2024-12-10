package com.example.rentalmanagement.Screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.AddressViewModel
import com.example.rentalmanagement.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var rcv: RecyclerView
    private val viewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fab = binding.homeFab
        rcv = binding.homeRcv

//        lifecycleScope.launch {
//            // Insert an address
//            val address = EntityAddress(id = 1, street = "123 Main St", city = "Some City", zip = "12345")
//            db.addressDAO().insertAddress(address)
//
//            // Fetch all addresses
//            val addresses = db.addressDAO().getAddress()
//            addresses.forEach {
//                println("Address: ${it.street}, ${it.city}, ${it.zip}")
//            }
//        }

        viewModel.address.observe(this) {address ->
            Log.d("View Model Address", "onCreate: $address")
        }

        fab.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        // Create the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)

        // Inflate the layout for the Bottom Sheet (this is the correct layout)
        val bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottomsheet_add_address, null)

        // Set the content view for the BottomSheetDialog
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

}
