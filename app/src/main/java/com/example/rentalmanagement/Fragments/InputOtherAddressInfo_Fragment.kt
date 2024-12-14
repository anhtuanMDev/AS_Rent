package com.example.rentalmanagement.Fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.rentalmanagement.Database.AddressRepo
import com.example.rentalmanagement.Database.DatabaseInstance
import com.example.rentalmanagement.Models.EntityAddress
import com.example.rentalmanagement.R
import com.example.rentalmanagement.databinding.FragmentInputOtherAddressInfoBinding
import java.io.File

class InputOtherAddressInfo_Fragment : Fragment() {
    private lateinit var bind: FragmentInputOtherAddressInfoBinding
    private lateinit var dao: AddressRepo

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            // Handle permission results
            if (results[READ_MEDIA_IMAGES] == true || results[READ_EXTERNAL_STORAGE] == true || results[READ_MEDIA_VIDEO] == true) {
                pickImageLauncher.launch("image/*")
            } else {
                Toast.makeText(activity, "PERMISION DENIED", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentInputOtherAddressInfoBinding.bind(
            inflater.inflate(
                R.layout.fragment_input_other_address_info_,
                container,
                false
            )
        )

        bind.addImgHouse.setOnClickListener { requestImagePermission() }
        val db = activity?.let { DatabaseInstance.getDatabase(it) }
        dao = db?.let { AddressRepo(it.addressDao()) }!!

        bind.btnFinish.setOnClickListener({
            dao.insertNewHouse(
                EntityAddress(
                    ward = "ward",
                    street = "street",
                    houseNumber = "houseNumber",
                    imagePath = "imagePath",
                    district = "district",
                    city = "city",
                    country = "country",
                    departmentType = "departmentType",
                    room = 2
                )
            )
        })
        return bind.root;
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                createImage(uri, "image${System.currentTimeMillis()}.webp")
            } else {
                Toast.makeText(activity, "No image selected", Toast.LENGTH_SHORT).show()
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

    private fun createImage(uri: Uri, name: String) {
        val cache = File(requireContext().cacheDir, "images");
        if (!cache.exists()) {
            cache.mkdir()
        }

        try {

            val image = File(cache, name)
            val bitmap = requireContext().contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, image.outputStream())
            } else {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 80, image.outputStream())
            }
            val webpUri = Uri.fromFile(image)
            bind.addImgHouse.setImageURI(webpUri)
        } catch (e: Exception) {
            Toast.makeText(activity, "Failed to convert to WebP: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }

    }
}