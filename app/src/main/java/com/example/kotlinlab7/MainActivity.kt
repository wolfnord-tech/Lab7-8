package com.example.kotlinlab7

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editTextText)
        imageView = findViewById(R.id.imageView)

        val button_to_add: Button = findViewById(R.id.button_to_add)
        button_to_add.setOnClickListener {
            val url = editText.text.toString()
            main_finctional(url)
        }
    }
    fun main_finctional(url:String){

        if (cool_URL(url)) {
            clean_editText()
            GlobalScope.launch(Dispatchers.IO) {
                Log.i("Карутина1", "Карутина1 запущена")

                try {
                    val imageBitmap = BitmapFactory.decodeStream(URL(url).openStream())
                    Log.i("Фотка", "Фото получено")
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(imageBitmap)
                        Log.i("Фотка", "Фото отображено")

                        GlobalScope.launch(Dispatchers.IO) {
                            Log.i("Карутина2", "Карутина2 запущена")
                            joinAll()
                            saveImageToGallery(imageBitmap)
                            Log.i("Сохранение", "Фото сохранено")
                        }

                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Log.i("Фотка", "Ошибка при загрузке изображения")
                        Toast.makeText(this@MainActivity, "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun clean_editText():Boolean{
        editText = findViewById(R.id.editTextText)
        editText.setText("")
        return true
    }



    fun cool_URL(url:String): Boolean{
        val containsSubString = url.contains(".jpg")
        if (!containsSubString) {
            Log.i("Фотка", "Неправильное раcширение")
            Toast.makeText(this, "Неправильное раcширение", Toast.LENGTH_SHORT).show()
            return false
        }
        Log.i("Фотка", "URL полученно")
        return true
    }


    fun saveImageToGallery(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "YourAppName")
        }
        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream = resolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
    }
}
