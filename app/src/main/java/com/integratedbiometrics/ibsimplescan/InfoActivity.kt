package com.integratedbiometrics.ibsimplescan

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.integratedbiometrics.ibsimplescan.utils.Constants
import com.integratedbiometrics.ibsimplescan.utils.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

fun Bitmap.toByteArray(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap? {
    return BitmapFactory.decodeByteArray(this, 0, size)
}

class InfoActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        Objects.requireNonNull(this.supportActionBar)?.hide()

        findViewById<View>(R.id.back).setOnClickListener { finish() }


        findViewById<Button>(R.id.printButton).setOnClickListener {
                        val intent = Intent(this, USBPrinterActivityTest::class.java)

            // Start the new activity
            startActivity(intent)
        }

        if (Constants.scanByte != null) {
            // Convert the byte array to a Bitmap
      //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  `      val bitmap: Bitmap =
        //        BitmapFactory.decodeByteArray(, 0, 255 )
            // Set the Bitmap to the ImageView
            if(Constants.scanByte!!.size > 0){
                Log.d("TAG", "run: called imgpreview finalbyte1: ${Constants.scanByte!![0]}")
                findViewById<ImageView>(R.id.imageView).setImageBitmap(Constants.scanByte!![0])

            }
            if(Constants.scanByte!!.size > 1){
                Log.d("TAG", "run: called imgpreview finalbyte2: ${Constants.scanByte!![1]}")
                findViewById<ImageView>(R.id.imageView2).setImageBitmap(Constants.scanByte!![1])

            }

            if(Constants.scanByte!!.size > 2){
                Log.d("TAG", "run: called imgpreview finalbyte3: ${Constants.scanByte!![2]}")
                findViewById<ImageView>(R.id.imageView3).setImageBitmap(Constants.scanByte!![2])

            }

        }



        if(Constants.picture != null){
            val imageView = findViewById<ImageView>(R.id.imageViewCameraTwo)
            imageView.setImageBitmap(Constants.picture )
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


        if(Constants.passportNumber.isNotEmpty()){

        }


//         val userDataDao = AppDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            // Create UserData object
            val userData = UserData(
                passportNumber = Constants.passportNumber,
                featureCamera = Constants.featureCamera,
                fingerFeatureOne = if((Constants.fingerFeature?.size ?:0 ) > 0)  Constants.fingerFeature?.get(0) else null,
                fingerFeatureTwo = if((Constants.fingerFeature?.size ?:0 ) > 1)  Constants.fingerFeature?.get(1) else null,
                fingerFeatureThree = if((Constants.fingerFeature?.size ?:0 ) > 2) Constants.fingerFeature?.get(2) else null,
                scanByteOne = if((Constants.scanByte?.size ?:0 ) > 0)  Constants.scanByte?.get(0)?.toByteArray()  else null,
                scanByteTwo = if((Constants.scanByte?.size ?:0 ) > 1)  Constants.scanByte?.get(1)?.toByteArray() else null,
                scanByteThree = if((Constants.scanByte?.size ?:0 ) > 2)  Constants.scanByte?.get(2)?.toByteArray() else null,
                cameraByte =  Constants.cameraByte,
                passportName = Constants.passportName,
                nationalityName =  Constants.nationalityName,
                dobValue = Constants.dobValue,
                expirationName = Constants.expirationName,
                enrollmentTime = System.currentTimeMillis()
            )

            // Insert or update the UserData
         //   userDataDao.userDataDao().insertOrUpdate(userData)

//            // Retrieve data
//            val retrievedUserData = userDataDao.getUserDataByPassportNumber("123456789")
//            println(retrievedUserData)

    }
}

}