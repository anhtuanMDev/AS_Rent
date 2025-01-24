package com.example.rentalmanagement.Screen

import android.content.Context
import android.graphics.Bitmap
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
import com.example.rentalmanagement.ViewModels.CreateRegisterFormViewModels
import com.example.rentalmanagement.databinding.ActivityRegisterTemporaryBinding
import java.io.File
import java.io.FileOutputStream

class RegisterTemporaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterTemporaryBinding
    private lateinit var formVM: CreateRegisterFormViewModels
    private val ROOM_ID: String = "id"
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
        val pdfFileName = R.raw.form_register // Replace "form_register" with your actual raw file name
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
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val aspectRatio = page.width.toFloat() / page.height.toFloat()
        val targetWidth: Int
        val targetHeight: Int

        if (screenWidth / aspectRatio <= screenHeight) {
            targetWidth = screenWidth
            targetHeight = (screenWidth / aspectRatio).toInt()
        } else {
            targetHeight = screenHeight
            targetWidth = (screenHeight * aspectRatio).toInt()
        }

        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val renderRect = android.graphics.Rect(0, 0, targetWidth, targetHeight)
        page.render(bitmap, renderRect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()

        // Create a new page in PdfDocument
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val newPage = pdfDocument.startPage(pageInfo)

        // Draw the original rendered bitmap onto the new PDF
        val canvas = newPage.canvas
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        // Set up Paint for text rendering
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 24f // Increased text size for better visibility
            isAntiAlias = true // Make the text look smoother
        }

        // Render main info onto the PDF
        if (main != null) {
            canvas.drawText("Name: ${main.name}", 100f, 100f, paint)
            canvas.drawText("Birthday: ${main.birth}", 100f, 130f, paint)
            canvas.drawText("Identification: ${main.identifyID}", 100f, 160f, paint)
            canvas.drawText("Gender: ${main.gender}", 100f, 190f, paint)
        } else {
            Log.e("PDF Error", "Main entity not found!")
        }

        // Render other info onto the PDF
        var yOffset = 220f
        others.forEach { person ->
            canvas.drawText("Name: ${person.name}", 100f, yOffset, paint)
            canvas.drawText("Birthday: ${person.birth}", 100f, yOffset + 30f, paint)
            canvas.drawText("Identification: ${person.identifyID}", 100f, yOffset + 60f, paint)
            canvas.drawText("Gender: ${person.gender}", 100f, yOffset + 90f, paint)
            canvas.drawText("Role: ${person.roleInHouse}", 100f, yOffset + 120f, paint)
            yOffset += 150f // Adjust the vertical spacing for the next entry
        }

        // Finish the page and save the new PDF
        pdfDocument.finishPage(newPage)
        val finalPdfFile = File(context.cacheDir, "final_form.pdf")
        pdfDocument.writeTo(FileOutputStream(finalPdfFile))
        pdfDocument.close()
        pdfRenderer.close()

        // Step 4: Render the final PDF for display in the ImageView
        val parcelFileDescriptor = ParcelFileDescriptor.open(finalPdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val finalRenderer = PdfRenderer(parcelFileDescriptor)
        val finalPage = finalRenderer.openPage(0)

        val finalBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        finalPage.render(finalBitmap, renderRect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        binding.registerTemporaryShowFile.setImageBitmap(finalBitmap)

        // Clean up resources
        finalPage.close()
        finalRenderer.close()
        parcelFileDescriptor.close()
    }




}