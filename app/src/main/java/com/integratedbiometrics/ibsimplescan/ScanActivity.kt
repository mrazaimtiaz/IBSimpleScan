package com.integratedbiometrics.ibsimplescan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import com.budiyev.android.codescanner.*

class ScanActivity : AppCompatActivity() {
 //   private lateinit var codeScanner: CodeScanner
    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
//        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
//
//        codeScanner = CodeScanner(this, scannerView)
//        var icBack = findViewById<ImageView>(R.id.ic_back)
//        icBack.setOnClickListener {
//            finish()
//        }
//        // Parameters (default values)
//        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
//        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
//        // ex. listOf(BarcodeFormat.QR_CODE)
//        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
//        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
//        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
//        codeScanner.isFlashEnabled = false // Whether to enable flash or not
//
//        // Callbacks
//        codeScanner.decodeCallback = DecodeCallback {
//            runOnUiThread {
//                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
//
////                val intent = Intent(this@ScanActivity, StudentInfoActivity::class.java)
////                intent.putExtra("tag",it.text)
////                startActivity(intent)
////                finish()
//            }
//        }
//        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
//            runOnUiThread {
//                Toast.makeText(this, "Camera initialization error: ${it.message}",
//                    Toast.LENGTH_LONG).show()
//            }
//        }
//
//        scannerView.setOnClickListener {
//            codeScaanner.startPreview()
//        }
    }

    override fun onResume() {
        super.onResume()
      //  codeScanner.startPreview()
    }

    override fun onPause() {
       // codeScanner.releaseResources()
        super.onPause()
    }
}