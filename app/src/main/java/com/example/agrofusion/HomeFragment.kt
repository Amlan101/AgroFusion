package com.example.agrofusion

import android.app.Activity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Define constants for camera-related functionality
    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101
    private val LOCATION_PERMISSION_CODE = 102

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setLocationName()

        val cardTakePicture = view.findViewById<MaterialCardView>(R.id.cardTakePicture)
        cardTakePicture.setOnClickListener {
            if(checkCameraPermission()){
                openCamera()
            }else{
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            }

        }

        return view
    }

    // This function is used to check for camera permission at runtime
    private fun checkCameraPermission(): Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false
        } else {
            // Permission is already granted
            return true
        }
    }

    // Function to check for location permission at runtime
    private fun checkLocationPermission(): Boolean{
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false
        } else {
            // Permission is already granted
            return true
        }
    }

    // This function is used to handle the result of the permission request.
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Success", "Permission to open camera granted successfully")
                    // Camera permission granted, check location permission
                    checkLocationPermission()
                } else {
                    // Camera permission denied
                    // Permission denied, show a message or handle accordingly
                    Log.d("Error", "Unable to open camera, since permission was denied.")
                }
            }

            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Success", "Permission to location granted successfully")
                    // Location permission granted, open camera
                    checkLocationPermission()
                } else {
                    // Location permission denied
                    Log.d("Error", "Permission denied.")
                }
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

    //Get the location name and set it in the app
    fun setLocationName(){
        if(checkLocationPermission()){
            fusedLocationClient.lastLocation.addOnSuccessListener {location: Location? ->
                location?.let {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.get(0)?.locality

                    view?.findViewById<TextView>(R.id.locationTextView)?.text = address
                }
            }
        }else{
            //Request it
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_CODE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDayAndDate()
    }

    private fun setDayAndDate() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val formattedDate = dateFormat.format(calendar.time)

        if(view != null){
            requireView().findViewById<TextView>(R.id.dateTextView).text = formattedDate
        }else{
            Log.d("ERROR", "The view is not drawn yet")
        }

    }
}