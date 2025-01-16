package com.example.rentalmanagement.Screen

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Adapters.HouseAdapter
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.HouseViewModels
import com.example.rentalmanagement.databinding.ActivityMainBinding
import com.example.rentalmanagement.databinding.BottomsheetAddAddressBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var rcv: RecyclerView
    private lateinit var adapter: HouseAdapter
    private var fileUri: Uri? = null
    private val TAG: String = "Activity Main log"
    private lateinit var houseVM: HouseViewModels
    private var bottomSheetBinding: BottomsheetAddAddressBinding? = null
    var apartmentTypes: List<String> = emptyList()

    private fun createImage(uri: Uri, name: String) {
        val cache = File(this.cacheDir, "images");
        if (!cache.exists()) {
            cache.mkdir()
        }

        try {
            val image = File(cache, name)
            val bitmap = this.contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, image.outputStream())
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, image.outputStream())
            }
            val webpUri = Uri.fromFile(image)
            fileUri = webpUri
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to convert to WebP: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun requestImagePermission() {
        // Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // Process the image in a background thread
                    createImage(uri, "image${System.currentTimeMillis()}.webp")

                    // Update the UI on the main thread
                    launch(Dispatchers.Main) {
                        if (fileUri != null) {
                            // Assuming `bottomSheetBinding` is accessible here or pass it as needed
                            bottomSheetBinding?.btsImg?.setImageURI(fileUri)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Failed to set image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }


    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            // Handle permission results
            if (results[READ_MEDIA_IMAGES] == true || results[READ_EXTERNAL_STORAGE] == true || results[READ_MEDIA_VIDEO] == true) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
            }
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
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        apartmentTypes = resources.getStringArray(R.array.apartment_type).toList()
        fab = binding.homeFab
        rcv = binding.homeRcv
        houseVM = ViewModelProvider(this@MainActivity)[HouseViewModels::class.java]
        adapter = HouseAdapter(houseVM)
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

        // Set the content view for the BottomSheetDialog using the binding's root
        bottomSheetDialog.setContentView(bottomSheetBinding?.root!!)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            apartmentTypes
        )

        bottomSheetBinding?.btsEdtApartmentType!!.setAdapter(adapter)
        bottomSheetBinding?.btsEdtApartmentType?.setText(apartmentTypes[0])
        bottomSheetBinding?.btsImg?.setOnClickListener({
            lifecycleScope.launch(Dispatchers.IO) {
                requestImagePermission()
            }
        })

        addValidation(bottomSheetBinding!!)

        bottomSheetBinding?.btsBtnFinish!!.setOnClickListener {
            if (!validate(bottomSheetBinding!!)) {
                return@setOnClickListener
            } else {
                val address = bottomSheetBinding!!.btsEdtAddress.text.toString()
                val rooms = bottomSheetBinding!!.btsEdtRooms.text.toString().toInt()
                val price = bottomSheetBinding!!.btsEdtPrice.text.toString().toInt()
                val departmentType = bottomSheetBinding!!.btsEdtApartmentType.text.toString()
                houseVM.addHouse(
                    EntityAddress(
                        0,
                        fileUri.toString(),
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

    private fun addValidation(bind: BottomsheetAddAddressBinding) {

        bind.btsEdtAddress.addTextChangedListener {
            if (it.toString().isEmpty()) {
                bind.btsEdtAddress.error = "Địa chỉ không được để trống"
            } else bind.btsEdtAddress.error = null
        }

        bind.btsEdtRooms.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val rooms = editable.toString().toInt()
                    when (bind.btsEdtApartmentType.text.toString()) {
                        apartmentTypes[0] -> {
                            if (rooms > 15 || rooms < 0) {
                                bind.btsEdtRooms.error = "Số phòng tối đa cho phòng trọ là 15"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }

                        apartmentTypes[1] -> {
                            if (rooms > 10) {
                                bind.btsEdtRooms.error = "Số phòng tối đa cho nhà là 10"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }

                        apartmentTypes[2] -> {
                            if (rooms < 10) {
                                bind.btsEdtRooms.error = "Số phòng tối thiểu cho chung cư là 10"
                            } else {
                                bind.btsEdtRooms.error = null
                            }
                        }
                    }
                } catch (e: NumberFormatException) {
                    bind.btsEdtRooms.error = "Vui lòng nhập số phòng hợp lệ"
                }
            } else {
                bind.btsEdtRooms.error = "Số phòng không được để trống"
            }
        }

        bind.btsEdtPrice.addTextChangedListener { editable ->
            if (editable.toString().isNotEmpty()) {
                try {
                    val price = editable.toString().toInt()
                    if (price < 1000000) {
                        bind.btsEdtPrice.error = "Giá thuê tối thiểu là 1 triệu"
                    } else {
                        bind.btsEdtPrice.error = null
                    }
                } catch (e: NumberFormatException) {
                    bind.btsEdtPrice.error = "Vui lòng nhập giá thuê hợp lệ"
                }
            } else {
                bind.btsEdtPrice.error = "Giá thuê không được để trống"
            }
        }
    }

    private fun validate(bind: BottomsheetAddAddressBinding): Boolean {
        if (bind.btsEdtAddress.error != null) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin địa chỉ", Toast.LENGTH_SHORT).show()
            return false
        }
        if (bind.btsEdtRooms.error != null) {
            Toast.makeText(this, "Vui lòng nhập đúng số lượng phòng", Toast.LENGTH_SHORT).show()
            return false
        }
        if (bind.btsEdtPrice.error != null) {
            Toast.makeText(this, "Vui lòng nhập đúng giá thuê", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
