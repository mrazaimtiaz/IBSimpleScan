package com.integratedbiometrics.ibsimplescan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.integratedbiometrics.ibsimplescan.utils.Constants

class InfoActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)


        findViewById<View>(R.id.back).setOnClickListener { finish() }

        if (Constants.scanByte != null) {
            // Convert the byte array to a Bitmap
      //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  `      val bitmap: Bitmap =
        //        BitmapFactory.decodeByteArray(, 0, 255 )
            // Set the Bitmap to the ImageView
            findViewById<ImageView>(R.id.imageView).setImageBitmap(Constants.scanByte)


        }
        findViewById<TextView>(R.id.textPassoert).setText(Constants.MRZ_GLOBAL)
    }
}