package com.yezhennan.exp03

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Activity3 : ComponentActivity() {

    private lateinit var ivPhoto: ImageView
    private var photoUri: Uri? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                photoUri?.let { uri ->
                    val bmp = contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                    ivPhoto.setImageBitmap(bmp)
                    Toast.makeText(this, R.string.toast_photo_success, Toast.LENGTH_SHORT).show()
                } ?: run {
                    val bmp = result.data?.extras?.get("data") as? Bitmap
                    bmp?.let { ivPhoto.setImageBitmap(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, R.string.toast_photo_fail, Toast.LENGTH_SHORT).show()
            }
        } else {
            android.util.Log.d("Activity3", "resultCode = ${result.resultCode}, data = ${result.data}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (16 * resources.displayMetrics.density + 0.5f).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        val btn = Button(this).apply {
            setText(R.string.btn_take_photo)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        ivPhoto = ImageView(this).apply {
            contentDescription = getString(R.string.iv_desc)
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            ).apply { topMargin = pad }
        }

        root.addView(btn)
        root.addView(ivPhoto)
        setContentView(root)

        btn.setOnClickListener { dispatchTakePicture() }
    }

    private fun dispatchTakePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {

                photoUri = FileProvider.getUriForFile(
                    this,
                    "com.yezhennan.exp03.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraLauncher.launch(intent)
            }
        }
    }

    private fun createImageFile(): File? = try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val storageDir = getExternalFilesDir("images")
        File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}