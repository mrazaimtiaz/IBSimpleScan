package com.integratedbiometrics.ibsimplescan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.s10.test.UsbSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val REQUEST_PERMISSIONS_CODE = 11
class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            var permissions: MutableList<String?>? = null
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !==
                PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("TAG", "onCreate: called permission requestPermissions: request permission")
                permissions = ArrayList()
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }else{

                Log.d("TAG", "granted onCreate: called permission requestPermissions: request permission")
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) !==
                PackageManager.PERMISSION_GRANTED
            ) {
                if (permissions == null) {
                    permissions = ArrayList()
                }
                permissions.add(Manifest.permission.CAMERA)
            }
            if (permissions == null) {
            } else {
                val permissionArray = permissions.toTypedArray()
                // Request the permission. The result will be received
                // in onRequestPermissionResult()
                requestPermissions(permissionArray, REQUEST_PERMISSIONS_CODE)
            }
        } else {
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

     val REQUEST_WRITE_PERMISSION = 786

        getSupportActionBar()?.hide()

        findViewById<Button>(R.id.btnBiometric).setOnClickListener {
            val intent = Intent(this@MainActivity, SimpleScanActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnMzr).setOnClickListener {
            val intent = Intent(this@MainActivity, PassportActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btnPrint).setOnClickListener {
            val intent = Intent(this@MainActivity, USBPrinterActivity::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btnCivilId).setOnClickListener {
            val intent = Intent(this@MainActivity, CivilIdActivity::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.btnIrisCamera).setOnClickListener {
            val intent = Intent(this@MainActivity, IrisActivity::class.java)
            startActivity(intent)


        }
        // Check and request write permission
      //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("TAG", "onCreate: called permission")
            requestPermissions()
      //  }
        findViewById<Button>(R.id.btnNfc).setOnClickListener {
            val intent = Intent(this@MainActivity, NfcActivity::class.java)
            startActivity(intent)
        }

       var usbSession = UsbSession()
  CoroutineScope(Dispatchers.IO).launch {
      usbSession.irisPower(1)
      usbSession.fingerPower(1)
      usbSession.cameraPower(1)
    //  usbSession.usbPower(0)
  }
        // Check if camera permission is granted, request if not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission()
        } else {
            // Camera permission already granted, proceed with camera-related operations
        }
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

   override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       if (requestCode == REQUEST_PERMISSIONS_CODE) {
            // Request for WRITE_EXTERNAL_STORAGE permission.
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission request was denied.
                Toast.makeText(this, R.string.txt_error_permission, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}