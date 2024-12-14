package com.example.rentalmanagement.Screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Adapters.HouseAdapter
import com.example.rentalmanagement.Database.AddressRepo
import com.example.rentalmanagement.Database.DatabaseInstance
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.AddressViewModel
import com.example.rentalmanagement.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var rcv: RecyclerView
    private val viewModel: AddressViewModel by viewModels()
    private lateinit var repo: AddressRepo
    private lateinit var adapter: HouseAdapter
    private val TAG: String = "Activity Main log"


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
        val db = DatabaseInstance.getDatabase(this@MainActivity)
        repo = AddressRepo(db.addressDao())

        adapter = HouseAdapter(emptyList())
        rcv.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rcv.adapter = adapter

        // Load data asynchronously and update the adapter
        lifecycleScope.launch(Dispatchers.IO) {
            val addresses = repo.getAllHouse()
            Log.d(TAG, "onCreate: House Data = " + addresses.toString())
            launch(Dispatchers.Main) {
                adapter.updateData(addresses)
            }
        }

        viewModel.address.observe(this) {address ->
            Log.d(TAG, "onCreate: INPUT = $address")
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
