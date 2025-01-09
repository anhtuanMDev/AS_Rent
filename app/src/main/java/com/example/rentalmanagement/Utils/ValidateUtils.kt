package com.example.rentalmanagement.Utils

import android.net.Uri
import java.io.File

class ValidateUtils {
    fun isLocalFileExists(uri: Uri): Boolean {
        return try {
            val file = File(uri.path ?: return false)
            file.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}