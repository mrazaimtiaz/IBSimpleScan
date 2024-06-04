//package com.integratedbiometrics.ibsimplescan
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.View
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import com.elyctis.idtabsdk.mrz.MrzScanner
//import com.example.s10.test.UsbSession
//import com.integratedbiometrics.ibsimplescan.utils.Constants.MRZ_GLOBAL
//import com.integratedbiometrics.ibsimplescan.utils.Constants.NAME_GLOBAL
//import com.integratedbiometrics.ibsimplescan.utils.MrzParser
//import com.integratedbiometrics.ibsimplescan.utils.MyMrzParser
//import kotlinx.coroutines.*
//import java.util.concurrent.Executors
//import java.util.concurrent.ScheduledExecutorService
//import java.util.concurrent.TimeUnit
//
//class PassportActivity : AppCompatActivity() {
//
//    private lateinit var scheduler: ScheduledExecutorService
//    override fun onDestroy() {
//        super.onDestroy()
//        if (this::scheduler.isInitialized) {
//            scheduler.shutdown() // Stop the scheduler when the activity is destroyed
//        }
//    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_passport)
//
//        getSupportActionBar()?.hide()
//
//        findViewById<View>(R.id.back).setOnClickListener { finish() }
//
//
//        findViewById<View>(R.id.home).setOnClickListener {
//            val intent = Intent(this@PassportActivity, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        var usbSession = UsbSession()
//        CoroutineScope(Dispatchers.IO).launch {
//            usbSession.irisPower(1)
//            usbSession.fingerPower(1)
//            usbSession.cameraPower(1)
//            //
//        }
//
//        initMzr()
//        //    val handler = Handler()
//        //  scheduler = Executors.newSingleThreadScheduledExecutor()
////        scheduler.scheduleAtFixedRate({
////            runOnUiThread {
////                readContinue()
////            }
////        }, 0, 1000, TimeUnit.MILLISECONDS)
//
//        findViewById<Button>(R.id.ReadNowBtn).setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                readContinue()
//
//            }
//        }
//        findViewById<Button>(R.id.fingerPrint).setOnClickListener {
//            val intent = Intent(this@PassportActivity, SimpleScanActivity::class.java)
//            startActivity(intent)
//        }
//
//        CoroutineScope(Dispatchers.IO).launch {
//            readContinueLoop()
//
//        }
//    }
//
//    private var mScanner: MrzScanner? = null
//
//    suspend fun readContinueLoop(){
//        readContinue()
//
//    }
//
//    fun initMzr(){
//        // mScanner = scanner
//
//        mScanner = MrzScanner(applicationContext, null)
//    }
//
//
//    fun readNow(){
//
//    }
//    suspend fun readContinue() {
//        Log.d("TAG", "readContinuemrz0: ")
//        if (mScanner?.open() == true) {
//            Log.d("TAG", "readContinuemrz1:")
//            var mrz = ""
//            try {
//                mrz = mScanner?.readMrz() ?: ""
//
//                Log.d("TAG", "readContinuemrz: $mrz")
//                if(mrz.trim().isNotEmpty()){
//                    Log.d("TAG", "readContinuemrzz: $mrz")
//                    withContext(Dispatchers.Main){
//                        findViewById<TextView>(R.id.textPassoertMzr).setText(mrz)
//
//                    }
//
//                }
//                var text = ""
//                if (mrz.trim { it <= ' ' }.length == 0) {
//
//                    text = "No decodable MRZ Found"
//
//                    withContext(Dispatchers.Main){
//                        findViewById<TextView>(R.id.textPassoert).setText(text)
//
//                    }
//
//                } else {
//                    text = mrz.replace('\r', '\n')
//
//                    NAME_GLOBAL =  text
//                    var record = MrzParser.parse(mrz.replace('\r', '\n'));
//// record.dateOfBirth +
//
//                    text = mrz.replace('\r', '\n') + "\n" +   "Name: " + record.givenNames + " " + record.surname + "\n" + " " +  "\n" +  "Nationality: " + record.nationality + " " +  "\n" +  "Expire Date: " +record.expirationDate + " "  +  "\n" +  "Gender: " +record.sex + " "
//                    System.out.println("Name: " + record.givenNames + " " + record.surname);
//
//                    NAME_GLOBAL =  record.givenNames
//                    MRZ_GLOBAL = text;
//                    findViewById<ImageView>(R.id.machineImage).visibility=View.GONE
//                }
//
//                withContext(Dispatchers.Main){
//                    findViewById<TextView>(R.id.textPassoert).setText(text)
//
//
//                }
//
//
//                //   _stateSelectService.value = stateSelectService.value.copy(success = mrz)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            mScanner?.close()
//            if (mrz.trim { it <= ' ' }.length == 0) {
//                //    tvMrz.setText(R.string.error_mrzscanner_nomrz)
//            } else {
//                //   tvMrz.setText(mrz.replace('\r', '\n'))
//            }
//        } else {
//            //  tvMrz.setText(R.string.error_mrzscanner_conn)
//        }
//
//        //  delay(100)
//        //  readContinueLoop()
//    }
//
//}