package com.example.rentalmanagement.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class CamerasUtils(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val pickImageLauncher: ActivityResultLauncher<String>,
    private val requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
) {
    var fileUri: Uri? = null
    var imageView: ImageView? = null

    fun createImage(uri: Uri, name: String) {
        val cache = File(context.cacheDir, "images")
        if (!cache.exists()) {
            cache.mkdir()
        }

        try {
            val image = File(cache, name)
            val bitmap = context.contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
            val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
            bitmap.compress(format, 80, image.outputStream())
            fileUri = Uri.fromFile(image)
        } catch (e: Exception) {
            MainScope().launch {
                Toast.makeText(context, "Failed to convert to WebP: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissionsLauncher.launch(
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO)
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionsLauncher.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
        } else {
            requestPermissionsLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
        }
    }

    fun handleImageResult(uri: Uri?) {
        if (uri != null) {
            MainScope().launch(Dispatchers.IO) {
                createImage(uri, "image${System.currentTimeMillis()}.webp")

                MainScope().launch(Dispatchers.Main) {
                    if (fileUri != null) {
                        imageView!!.setImageURI(fileUri)
                    } else {
                        Toast.makeText(
                            context,
                            "Failed to set image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun handlePermissionResult(granted: Map<String, Boolean>) {
        if (granted[READ_MEDIA_IMAGES] == true || granted[READ_EXTERNAL_STORAGE] == true) {
            pickImageLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
        }
    }
}
