package com.integratedbiometrics.ibsimplescan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.s10.test.UsbSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with camera-related operations
            } else {
                // Camera permission denied, handle accordingly (e.g., display a message or request again)
            }
        }
    }

}