package com.example.rentalmanagement.Screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rentalmanagement.Models.EntityPeople
import com.example.rentalmanagement.R
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.ROOM_ID
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.TAG_LOG
import com.example.rentalmanagement.ViewModels.CreateRegisterFormViewModels
import com.example.rentalmanagement.databinding.ActivityRegisterTemporaryBinding
import java.io.File
import java.io.FileOutputStream

class RegisterTemporaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterTemporaryBinding
    private lateinit var formVM: CreateRegisterFormViewModels
    private lateinit var extras: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_temporary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityRegisterTemporaryBinding.inflate(layoutInflater)
        extras = intent.extras!!

        formVM = CreateRegisterFormViewModels(application)
        formVM.getInfo(extras.getInt(ROOM_ID)).observe(this){
            createAndDisplayForm(this, it)
        }
        binding.registerTemporaryShowFile
        setContentView(binding.root)
    }

    private fun createAndDisplayForm(context: Context, list: List<EntityPeople>) {
        // Step 1: Find the main entity (e.g., "Chủ Hộ")
        val main = list.find { it.roleInHouse == "Chủ Hộ" }
        val others = list.filter { it != main } // Get the rest of the list

        // Step 2: Load the PDF from the raw folder
        val pdfFileName = R.raw.form_register
        val pdfFile = File(context.cacheDir, "temp_form.pdf") // Save the raw resource to a temporary file
        context.resources.openRawResource(pdfFileName).use { inputStream ->
            FileOutputStream(pdfFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        // Step 3: Create a new PdfDocument and render text onto it
        val pdfDocument = PdfDocument()
        val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY))
        val page = pdfRenderer.openPage(0) // Render the first page of the original PDF

        // Get screen dimensions for scaling
        val screenWidth = binding.registerTemporaryShowFile.width * 0.8
        val screenHeight = binding.registerTemporaryShowFile.height
        val aspectRatio = page.width.toFloat() / page.height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (screenWidth / aspectRatio <= screenHeight) {
            targetWidth = screenWidth.toInt()
            targetHeight = (screenWidth / aspectRatio).toInt()
        } else {
            targetHeight = screenHeight
            targetWidth = (screenHeight * aspectRatio).toInt()
        }

        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val renderRect = android.graphics.Rect(0, 0, targetWidth, targetHeight)
        page.render(bitmap, renderRect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        binding.registerTemporaryShowFile.setImageBitmap(bitmap)
    }

}