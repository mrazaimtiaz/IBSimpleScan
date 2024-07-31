//package com.example.yourapp
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import android.provider.MediaStore
//import android.util.Log
//import android.util.Size
//import android.view.View
//import android.widget.Button
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.*
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//
//class CameraActivity : AppCompatActivity() {
//
//    private lateinit var previewView: PreviewView
//    private lateinit var captureButton: Button
//    private var imageCapture: ImageCapture? = null
//
//    private val PERMISSION_REQUEST_CODE = 1001
//    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView( com.integratedbiometrics.ibsimplescan.R.layout.activity_main)
//
//        previewView = findViewById(com.integratedbiometrics.ibsimplescan.R.id.previewView)
//        captureButton = findViewById(com.integratedbiometrics.ibsimplescan.R.id.captureButton)
//
//        if (arePermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE)
//        }
//
//        captureButton.setOnClickListener {
//            takePhoto()
//        }
//    }
//
//    private fun arePermissionsGranted(): Boolean {
//        return REQUIRED_PERMISSIONS.all { permission ->
//            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(previewView.surfaceProvider)
//                }
//
//            imageCapture = ImageCapture.Builder()
//                .setTargetResolution(Size(640, 480)) // Adjust as needed
//                .build()
//
//            val cameraSelector = CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build()
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)
//            } catch (e: Exception) {
//                Log.e("MainActivity", "Use case binding failed", e)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun takePhoto() {
//        val imageCapture = imageCapture ?: return
//
//        val photoFile = createFile()
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                val savedUri = Uri.fromFile(photoFile)
//                Toast.makeText(this@CameraActivity, "Photo saved: $savedUri", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Log.e("MainActivity", "Photo capture failed: ${exception.message}", exception)
//                Toast.makeText(this@CameraActivity, "Photo capture failed.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun createFile(): File {
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
//        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile("JPEG_$timeStamp", ".jpg", storageDir)
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                startCamera()
//            } else {
//                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
