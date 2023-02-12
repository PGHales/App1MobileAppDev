package com.example.app_1

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private var firstNameInput: EditText? = null
    private var middleNameInput: EditText? = null
    private var lastNameInput: EditText? = null
    private var profilePicturePath: String? = null
    private var profilePictureView: ImageView? = null
    private var profilePictureProvided: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        firstNameInput = findViewById(R.id.firstNameInput)
        middleNameInput = findViewById(R.id.middleNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        profilePictureView = findViewById(R.id.imageView)
        profilePicturePath = String.format("%s/profilePicture.png", getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("firstName", firstNameInput!!.text.toString())
        outState.putString("middleName", middleNameInput!!.text.toString())
        outState.putString("lastName", lastNameInput!!.text.toString())
        outState.putBoolean("profilePictureProvided", profilePictureProvided)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        firstNameInput!!.setText(savedInstanceState.getString("firstName"))
        middleNameInput!!.setText(savedInstanceState.getString("middleName"))
        lastNameInput!!.setText(savedInstanceState.getString("lastName"))
        profilePictureProvided = savedInstanceState.getBoolean("profilePictureProvided")
        loadProfilePicture()
    }

    fun takePicture(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(intent)
    }

    fun submit(view: View) {
        val showProfileIntent = Intent(this, ShowProfileActivity::class.java)
        showProfileIntent.putExtra("firstName", firstNameInput!!.text.toString())
        showProfileIntent.putExtra("middleName", middleNameInput!!.text.toString())
        showProfileIntent.putExtra("lastName", lastNameInput!!.text.toString())
        showProfileIntent.putExtra("profilePictureProvided", profilePictureProvided)
        startActivity(showProfileIntent)
    }

    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            //If statement needed to prevent error caused by getParcelableExtra being changed in API level 33. Need to have both versions of the method just in case
            val profilePicture: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data!!.getParcelableExtra("data", Bitmap::class.java)!!
            } else {
                it.data!!.getParcelableExtra("data")!!
            }
            profilePictureView!!.setImageBitmap(profilePicture)
            //Try to save it, if possible
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                saveProfilePicture(profilePicture)
                profilePictureProvided = true
            }
        }
    }

    fun saveProfilePicture(profilePicture: Bitmap) {
        val imageFolder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
        //Make folder(s) for picture it doesn't exist
        imageFolder.mkdirs()
        val imageFile = File(profilePicturePath!!)
        //Delete old picture if one exists
        imageFile.delete()
        //Compress picture to PNG, using syntax akin to try-with-resources from Java
        FileOutputStream(imageFile).use {
            profilePicture.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun loadProfilePicture() {
        //Only load image if it exists and has been provided by the current user
        if (profilePictureProvided && File(profilePicturePath!!).exists()) {
            profilePictureView!!.setImageBitmap(BitmapFactory.decodeFile(profilePicturePath!!))
        }
    }





}