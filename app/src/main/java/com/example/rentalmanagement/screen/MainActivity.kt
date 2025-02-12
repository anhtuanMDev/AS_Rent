package com.example.rentalmanagement.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.adapters.HouseAdapter
import com.example.rentalmanagement.models.EntityAddress
import com.example.rentalmanagement.R
import com.example.rentalmanagement.utils.ValidateUtils
import com.example.rentalmanagement.viewModels.HouseViewModels
import com.example.rentalmanagement.databinding.ActivityMainBinding
import com.example.rentalmanagement.databinding.BottomsheetAddAddressBinding
import com.example.rentalmanagement.utils.CamerasUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var rcv: RecyclerView
    private lateinit var adapter: HouseAdapter
    private lateinit var houseVM: HouseViewModels
    private var bottomSheetBinding: BottomsheetAddAddressBinding? = null
    var apartmentTypes: List<String> = emptyList()
    private val validate = ValidateUtils()

    private lateinit var camerasUtils: CamerasUtils

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            camerasUtils.handleImageResult(uri)
        }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            camerasUtils.handlePermissionResult(granted)
        }

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
//        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        apartmentTypes = resources.getStringArray(R.array.apartment_type).toList()
        camerasUtils = CamerasUtils(
            context = this,
            lifecycleOwner = this,
            pickImageLauncher = pickImageLauncher,
            requestPermissionsLauncher = requestPermissionsLauncher
        )

        fab = binding.homeFab
        rcv = binding.homeRcv
        houseVM = ViewModelProvider(this@MainActivity)[HouseViewModels::class.java]
        adapter = HouseAdapter(houseVM, pickImageLauncher, this, requestPermissionsLauncher, apartmentTypes)
        houseVM.getData.observe(this) { data ->
            if (data.size == 0) {
                adapter.updateData(data)
                binding.emptyHouse.visibility = android.view.View.VISIBLE
                binding.homeRcv.visibility = android.view.View.GONE
            } else {
                adapter.updateData(data)
                binding.emptyHouse.visibility = android.view.View.GONE
                binding.homeRcv.visibility = android.view.View.VISIBLE
            }
        }
        rcv.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rcv.adapter = adapter

        fab.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        // Create the BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        // Use the correct way to inflate the BottomSheet layout using view binding
        bottomSheetBinding = BottomsheetAddAddressBinding.inflate(LayoutInflater.from(this))


        camerasUtils.imageView = bottomSheetBinding?.btsImg
        // Set the content view for the BottomSheetDialog using the binding's root
        bottomSheetDialog.setContentView(bottomSheetBinding?.root!!)

        val adapter = ArrayAdapter(
            this,
            R.layout.item_simple_text,
            apartmentTypes
        )

        bottomSheetBinding?.btsEdtApartmentType!!.setAdapter(adapter)
        bottomSheetBinding?.btsEdtApartmentType?.setText(apartmentTypes[0])
        bottomSheetBinding?.btsImg?.setOnClickListener({
            lifecycleScope.launch(Dispatchers.IO) {
                camerasUtils.requestImagePermission()
            }
        })

        validate.setErrorAddress(bottomSheetBinding!!, apartmentTypes)

        bottomSheetBinding?.btsBtnFinish!!.setOnClickListener {
            if (!validate.checkErrorAddAddress(bottomSheetBinding!!)) {
                return@setOnClickListener
            } else {
                val address = bottomSheetBinding!!.btsEdtAddress.text.toString()
                val rooms = bottomSheetBinding!!.btsEdtRooms.text.toString().toInt()
                val price = bottomSheetBinding!!.btsEdtPrice.text.toString().toInt()
                val departmentType = bottomSheetBinding!!.btsEdtApartmentType.text.toString()
                houseVM.addHouse(
                    EntityAddress(
                        0,
                        camerasUtils.fileUri.toString(),
                        address,
                        price,
                        departmentType,
                        rooms,
                        0,
                        "",
                        0.0
                    )
                )
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetDialog.show()
    }

}
