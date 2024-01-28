package com.example.agrofusion

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {

    // Define constants for camera-related functionality
    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val cardTakePicture = view.findViewById<MaterialCardView>(R.id.cardTakePicture)
        cardTakePicture.setOnClickListener {
            checkCameraPermission()
        }

        return view
    }

    // This function is used to check for camera permission at runtime
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            // Permission is already granted, open the camera
            openCamera()
        }
    }

    // This function is used to handle the result of the permission request.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                Log.d("Success","Permission to open camera granted successfully")
                openCamera()
            } else {
                // Permission denied, show a message or handle accordingly
                Log.d("Error","Unable to open camera, since permission was denied.")
            }
        }
    }

    // This function is used to open the camera using an explicit Intent
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    // This function is used to handle the captured image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle the captured image for ML processing
            val imageBitmap = data?.extras?.get("data") as Bitmap?
                imageBitmap?.let {
                    try {
                        // Define file location within app's internal storage
                        val file = File(requireContext().filesDir, "captured_image.jpg")
                        // Create OutputStream
                        val outputStream = FileOutputStream(file)
                        // Compress and write image data
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        // Close OutputStream
                        outputStream.close()
                        // Optionally, handle success (e.g., show a message to the user)
                    } catch (e: IOException) {
                        // Handle IOException (e.g., show error message)
                        Log.d("Error","IO exception")
                    }
                }
            }
    }


}