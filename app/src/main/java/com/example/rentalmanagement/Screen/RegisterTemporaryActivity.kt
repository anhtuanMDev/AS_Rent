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

        binding.registerTemporaryShowFile.setImageBitmap(bitmap)

        Log.d(TAG_LOG, "$main")
        Log.d(TAG_LOG, "${list.size}")
        Log.d(TAG_LOG, "$others")
//        createBitmapWithText(main!!, others)
    }

    private fun createBitmapWithText(mainInfo: EntityPeople, others: List<EntityPeople>) {
        // Step 1: Define the dimensions of the Bitmap
        val displayMetrics = this.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Step 2: Create a Canvas and Paint for drawing
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT) // Set the background color to white

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        Log.d(TAG_LOG, mainInfo.toString())

        // Step 3: Draw some sample text
        val sampleText = "Hello, this is a test!"
        val xOffset = 150f
        var yOffset = 100f

        // Draw multiple lines of text for testing
        canvas.drawText(sampleText, xOffset, yOffset, paint)
        yOffset += 60f
        canvas.drawText("Another line of text here.", xOffset, yOffset, paint)
        yOffset += 60f
        canvas.drawText("And one more for good measure.", xOffset, yOffset, paint)

//        // Step 4: Draw shapes to test rendering (Optional)
//        paint.color = Color.RED
//        canvas.drawRect(50f, yOffset + 20f, 300f, yOffset + 80f, paint)
//
//        paint.color = Color.BLUE
//        canvas.drawCircle(400f, yOffset + 50f, 30f, paint)

        // Step 5: Set the Bitmap to the ImageView
        binding.renderText.setImageBitmap(bitmap)
    }



}