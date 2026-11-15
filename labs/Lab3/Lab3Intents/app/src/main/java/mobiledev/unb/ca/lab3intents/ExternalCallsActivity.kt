package mobiledev.unb.ca.lab3intents

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import androidx.core.net.toUri
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class ExternalCallsActivity : AppCompatActivity() {
    // Attributes for storing the file photo path
    private lateinit var currentPhotoPath: String

    // Activity listeners
    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_external_calls)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Register the activity listener
        setCameraActivityResultLauncher()

        // Register the button listeners
        val cameraButton: Button = findViewById(R.id.btnCamera)
        cameraButton.setOnClickListener {
            dispatchTakePhotoIntent()
        }

        val emailButton: Button = findViewById(R.id.btnEmail)
        emailButton.setOnClickListener {
            dispatchSendEmailIntent()
        }

        val backButton = findViewById<Button>(R.id.btnBack)
        backButton.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            try {
//                startActivity(intent)
//            } catch (_: ActivityNotFoundException) {
//                Log.e(TAG, "Unable to start the activity")
//            }
            finish()
        }
    }

    // Email functions
    private fun dispatchSendEmailIntent() {
        // Handle the send email intent
        val recipient = arrayOf("sadiq.shahid101@unb.ca")
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, recipient)
            putExtra(Intent.EXTRA_SUBJECT, "CS2063 Lab 3")
            putExtra(Intent.EXTRA_TEXT, "This is a test email")
        }
        if (intent.resolveActivity(packageManager) != null) {
            val chooserIntent = Intent.createChooser(intent, "")
            startActivity(chooserIntent)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        Log.i(TAG, "INSIDE CREATE IMAGE FILE")
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.i(TAG, "ABOUT TO RETURN FROM CREATING IMAGE FILE")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    // Camera Functions
    private fun dispatchTakePhotoIntent() {
        // Handle the photo intent
        Log.i(TAG, "STARTED CAMERA FUNCTION")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    Log.i(TAG, "STARTING CREATE IMAGE FILE")
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e(TAG, "Error saving photo or something")
                    Log.i(TAG, "ERROR CREATING FILE FOR CAPTURED PHOTO")
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    Log.i(TAG, "PHOTO FILE CREATED SUCCESS")
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "$packageName.provider",
                        it
                    )
                    Log.i(TAG, "FOUND PHOTO URI")
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    cameraActivityResultLauncher?.launch(takePictureIntent)
                }
            }
        }
    }

    private fun setCameraActivityResultLauncher() {
        Log.i(TAG, "REGISTERING CAMERA")
        cameraActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                Log.i(TAG, "SUCCESSFULLY GOT RESULT FROM CAMERA")
                galleryAddPic()
            }
        }
    }

    private fun galleryAddPic() {
        Log.d(TAG, "Saving image to the gallery")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) and above
            mediaStoreAddPicToGallery()
        } else {
            // Pre Android 10
            mediaScannerAddPicToGallery()
        }
        Log.i(TAG, "Image saved!")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun mediaStoreAddPicToGallery() {
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)

        val contentValues = getContentValues()
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        contentValues.put(MediaStore.Images.Media.IS_PENDING, true)

        val resolver = contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let { uri ->
            // The use function is a Kotlin extension function that automatically closes
            // the OutputStream after the block of code inside it has executed.
            // This eliminates the need for a separate try-catch block to
            // close the outputStream.
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            } ?: Log.e(TAG, "Error saving the file: outputStream is null")

            contentValues.apply {
                put(MediaStore.Images.Media.IS_PENDING, false)
                resolver.update(uri, this, null, null)
            }
        }
    }

    private fun getContentValues() : ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
    }

    private fun mediaScannerAddPicToGallery() {
        val file = File(currentPhotoPath)
        MediaScannerConnection.scanFile(this@ExternalCallsActivity,
            arrayOf(file.toString()),
            arrayOf(file.name),
            null)
    }
    companion object {
        // String for LogCat documentation
        private const val TAG = "ExternalCallsActivity"
    }
}