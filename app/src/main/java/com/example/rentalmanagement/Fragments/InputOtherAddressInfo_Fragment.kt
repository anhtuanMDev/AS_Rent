package com.example.rentalmanagement.Fragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.rentalmanagement.R
import com.example.rentalmanagement.databinding.FragmentInputOtherAddressInfoBinding

class InputOtherAddressInfo_Fragment : Fragment() {
    private lateinit var bind: FragmentInputOtherAddressInfoBinding

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
        return bind.root;
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                // Handle the selected image URI
                bind.addImgHouse.setImageURI(uri) // Example: Show the image in an ImageView
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
}