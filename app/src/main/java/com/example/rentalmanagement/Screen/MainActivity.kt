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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rentalmanagement.Adapters.HouseAdapter
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.Models.RoomSmallDisplay
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.HouseViewModels
import com.example.rentalmanagement.databinding.ActivityMainBinding
import com.example.rentalmanagement.databinding.BottomsheetAddAddressBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fab: FloatingActionButton
    private lateinit var rcv: RecyclerView
    private lateinit var adapter: HouseAdapter
    private var fileUri: Uri? = null
    private val TAG: String = "Activity Main log"
    private lateinit var houseVM: HouseViewModels
    private var roomData: List<List<RoomSmallDisplay>> = emptyList()
    private var bottomSheetBinding: BottomsheetAddAddressBinding? = null

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

        fab = binding.homeFab
        rcv = binding.homeRcv
        houseVM = ViewModelProvider(this@MainActivity)[HouseViewModels::class.java]
        adapter = HouseAdapter()
        houseVM.getData.observe(this) { data ->
            adapter.updateData(data)
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

        ArrayAdapter.createFromResource(
            this,
            R.array.apartment_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            bottomSheetBinding?.btsSpinner!!.adapter = adapter
        }

        bottomSheetBinding?.btsImg?.setOnClickListener({
            lifecycleScope.launch(Dispatchers.IO) {
                requestImagePermission()
            }
        })

//        bottomSheetBinding?.btsSpinner!!.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    val selectedItem = parent?.getItemAtPosition(position).toString()
//                    Log.d(TAG, "Spinner selected item: $selectedItem")
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    Toast.makeText(this@MainActivity, "Nothing selected", Toast.LENGTH_SHORT).show()
//                    Log.d(TAG, "Spinner nothing selected")
//                }
//            }

        bottomSheetBinding?.btsBtnFinish!!.setOnClickListener {
            val address = bottomSheetBinding!!.btsEdtAddress.text.toString()
            val rooms = bottomSheetBinding!!.btsEdtRooms.text.toString().toInt()
            val price = bottomSheetBinding!!.btsEdtPrice.text.toString().toInt()
            val departmentType = bottomSheetBinding!!.btsSpinner.selectedItem.toString()
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
        bottomSheetDialog.show()
    }

}
