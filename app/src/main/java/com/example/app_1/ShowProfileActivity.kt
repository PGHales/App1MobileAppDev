package com.example.app_1

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import java.io.File

class ShowProfileActivity : AppCompatActivity() {

    private var loginText: TextView? = null
    private var profilePictureProvided: Boolean = false
    private var profilePicturePath: String? = null
    private var profilePictureView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        loginText = findViewById(R.id.loginText)
        profilePictureView = findViewById(R.id.imageView)
        profilePicturePath = String.format("%s/profilePicture.png", getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
        loginText!!.text = String.format("%s %s is logged in!",
            intent.getStringExtra("firstName"), intent.getStringExtra("lastName"))
        profilePictureProvided = intent.getBooleanExtra("profilePictureProvided", false)
        loadProfilePicture()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("loginText", loginText!!.text.toString())
        outState.putBoolean("profilePictureProvided", profilePictureProvided)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        loginText!!.text = savedInstanceState.getString("loginText")
        profilePictureProvided = savedInstanceState.getBoolean("profilePictureProvided")
        loadProfilePicture()
    }

    private fun loadProfilePicture() {
        //Only load image if it exists and was provided by the previous activity
        if (profilePictureProvided && File(profilePicturePath!!).exists()) {
            profilePictureView!!.setImageBitmap(BitmapFactory.decodeFile(profilePicturePath!!))
        }
    }
}