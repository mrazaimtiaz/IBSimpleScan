package com.integratedbiometrics.ibsimplescan

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.integratedbiometrics.ibsimplescan.utils.Constants
import java.util.*

class InfoActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        Objects.requireNonNull(this.supportActionBar)?.hide()

        findViewById<View>(R.id.back).setOnClickListener { finish() }

        if (Constants.scanByte != null) {
            // Convert the byte array to a Bitmap
      //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  `      val bitmap: Bitmap =
        //        BitmapFactory.decodeByteArray(, 0, 255 )
            // Set the Bitmap to the ImageView
            findViewById<ImageView>(R.id.imageView).setImageBitmap(Constants.scanByte)


        }
        if(Constants.cameraByte != null){
            // Convert decoded byte array to Bitmap

            // Convert decoded byte array to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(Constants.cameraByte, 0, Constants.cameraByte!!.size)

            // Display Bitmap in ImageView

            // Display Bitmap in ImageView
            if (bitmap != null) {
                val imageView = findViewById<ImageView>(R.id.imageViewCamera)
                imageView.setImageBitmap(bitmap)
            }
        }
        findViewById<TextView>(R.id.textPassoert).setText(Constants.MRZ_GLOBAL)
    }
}