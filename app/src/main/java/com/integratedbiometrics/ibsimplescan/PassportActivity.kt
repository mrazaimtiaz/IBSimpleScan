package com.integratedbiometrics.ibsimplescan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.elyctis.idtabsdk.io.Io
import com.elyctis.idtabsdk.mrz.MrzScanner
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity
import com.example.s10.test.UsbSession
import com.integratedbiometrics.ibsimplescan.utils.Constants.MRZ_GLOBAL
import com.integratedbiometrics.ibsimplescan.utils.Constants.NAME_GLOBAL
import com.integratedbiometrics.ibsimplescan.utils.MrzParser
import com.integratedbiometrics.ibsimplescan.utils.MyMrzParser
import com.integratedbiometrics.ibsimplescan.utils.UsbAttachedReceiver
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class PassportActivity : AppCompatActivity() {

    private lateinit var scheduler: ScheduledExecutorService

    private var mReceiver: BroadcastReceiver? = null

    private var mPendingIntent: PendingIntent? = null
    override fun onDestroy() {
        super.onDestroy()
        if (this::scheduler.isInitialized) {
            scheduler.shutdown() // Stop the scheduler when the activity is destroyed
        }
    }

    override fun onStart() {
        super.onStart()

        // Power ON MRZ Scanner
        Io.powerOnTopModule()
//        ElyDialogFragment.show(
//            getFragmentManager(), R.string.title_info,
//            R.string.text_waiting_peripheral, R.attr.actionModeFindDrawable
//        )
    }

    override fun onResume() {
        super.onResume()
        val intentfilter = IntentFilter()
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_ATTACHED)
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH)
        registerReceiver(mReceiver, intentfilter)

        // Set alarm to force module status refreshing
        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am[AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                2000] = mPendingIntent
    }

    override fun onPause() {
        super.onPause()
        val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(mPendingIntent)
        unregisterReceiver(mReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport)

            getSupportActionBar()?.hide()

        findViewById<View>(R.id.back).setOnClickListener { finish() }


        findViewById<View>(R.id.home).setOnClickListener {
            val intent = Intent(this@PassportActivity, MainActivity::class.java)
            startActivity(intent)
        }

        var usbSession = UsbSession()
        CoroutineScope(Dispatchers.IO).launch {
            usbSession.irisPower(1)
            usbSession.fingerPower(1)
            usbSession.cameraPower(1)
            //
        }

        initMzr()
    //    val handler = Handler()
      //  scheduler = Executors.newSingleThreadScheduledExecutor()
//        scheduler.scheduleAtFixedRate({
//            runOnUiThread {
//                readContinue()
//            }
//        }, 0, 1000, TimeUnit.MILLISECONDS)

        findViewById<Button>(R.id.ReadNowBtn).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                readMrz()
         //       readContinue()

            }
        }
        findViewById<Button>(R.id.fingerPrint).setOnClickListener {
            val intent = Intent(this@PassportActivity, SimpleScanActivity::class.java)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            readContinueLoop()

        }

        // Instead of doing multiple connection attempt to the scanner
        // we wait for broadcast messages
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action.equals(UsbAttachedReceiver.ACTION_MODULE_ATTACHED, ignoreCase = true) ||
                    action.equals(
                        UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH,
                        ignoreCase = true
                    )
                ) {
                    // Check connection and dismiss waiting message
                    val isOpen = mScanner!!.open()
                    //ElyDialogFragment.dismiss(getFragmentManager())
                    mScanner!!.close()
                    // Check status
                    if (!isOpen) {
                        Log.i("ElyMrzActivity", "No MRZ scanner detected.")
//                        ElyDialogFragment.show(
//                            getFragmentManager(), R.string.title_error,
//                            R.string.error_mrzscanner_conn, R.attr.alertDialogIcon
//                        )
                    }
                }
            }
        }
        mPendingIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH), 0
        )
        val usbPermissionActivity =
            UsbPermissionActivity(getApplicationContext(), " com.elyctis.idtabsuite")
        if (!usbPermissionActivity.isAllUsbDevicesAccessPermissionGranted) {
            usbPermissionActivity.getUsbDeviceAccessPermission()
        }
    }

    private var mScanner: MrzScanner? = null

   suspend fun readContinueLoop(){
          //  readContinue()
readMrz()
    }

    fun initMzr(){
       // mScanner = scanner

        mScanner = MrzScanner(applicationContext, null)
    }


    fun readNow(){

    }

   suspend fun readMrz() {
        if (mScanner!!.open()) {
            var mrz = ""
            try {
                mrz = mScanner!!.readMrz()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mScanner!!.close()
            var text = ""
            if (mrz.trim { it <= ' ' }.length == 0) {
                text = "No decodable MRZ Found"

                withContext(Dispatchers.Main){
                    findViewById<TextView>(R.id.textPassoert).setText(text)

                }
            }  else {
                text = mrz.replace('\r', '\n')

                NAME_GLOBAL =  text
                try{
                    var record = MrzParser.parse(mrz.replace('\r', '\n'));
// record.dateOfBirth +

                    text = mrz.replace('\r', '\n') + "\n" +   "Name: " + record.givenNames + " " + record.surname + "\n" + " " +  "\n" +  "Nationality: " + record.nationality + " " +  "\n" +  "Expire Date: " +record.expirationDate + " "  +  "\n" +  "Gender: " +record.sex + " "
                    System.out.println("Name: " + record.givenNames + " " + record.surname);

                    withContext(Dispatchers.Main){
                        findViewById<LinearLayout>(R.id.passportLayout).visibility = View.VISIBLE
                        findViewById<TextView>(R.id.name_value).setText( record.givenNames)
                        findViewById<TextView>(R.id.nationality_value).setText(record.nationality)
                        findViewById<TextView>(R.id.expiry_value).setText(record.expirationDate.day.toString() + "/" + record.expirationDate.month.toString() + "/"  +record.expirationDate.year.toString())
                        findViewById<TextView>(R.id.dob_value).setText(record.dateOfBirth.day.toString() + "/" + record.dateOfBirth.month.toString() + "/"  +record.dateOfBirth.year.toString())

                        findViewById<TextView>(R.id.gender_value).setText(record.sex.name)
                        findViewById<TextView>(R.id.document_value).setText(record.documentNumber)
                    }







                    NAME_GLOBAL =  record.givenNames
                    MRZ_GLOBAL = text;
                    withContext(Dispatchers.Main){
                        findViewById<TextView>(R.id.textPassoert).setText(text)
                        findViewById<ImageView>(R.id.machineImage).visibility=View.GONE

                    }
                }catch (e: java.lang.Exception){
                    Log.d("TAG", "readMrz: called mrz")
                    e.printStackTrace()
                    withContext(Dispatchers.Main){
                        findViewById<TextView>(R.id.textPassoert).setText(text)

                        findViewById<ImageView>(R.id.machineImage).visibility=View.GONE

                    }

                }

            }
        } else {
            withContext(Dispatchers.Main){
              //  findViewById<TextView>(R.id.textPassoert).setText("MRZ Scanner not found")

            }
            readMrz()
        }
    }
   suspend fun readContinue() {
        Log.d("TAG", "readContinuemrz0: ")
        if (mScanner?.open() == true) {
            Log.d("TAG", "readContinuemrz1:")
            var mrz = ""
            try {
                mrz = mScanner?.readMrz() ?: ""

                Log.d("TAG", "readContinuemrz: $mrz")
                    if(mrz.trim().isNotEmpty()){
                        Log.d("TAG", "readContinuemrzz: $mrz")
                        withContext(Dispatchers.Main){
                            findViewById<TextView>(R.id.textPassoertMzr).setText(mrz)

                        }

                    }
                var text = ""
                if (mrz.trim { it <= ' ' }.length == 0) {

                   text = "No decodable MRZ Found"

                    withContext(Dispatchers.Main){
                        findViewById<TextView>(R.id.textPassoert).setText(text)

                    }

                } else {
                   text = mrz.replace('\r', '\n')

                    NAME_GLOBAL =  text
                    var record = MrzParser.parse(mrz.replace('\r', '\n'));
// record.dateOfBirth +

                    text = mrz.replace('\r', '\n') + "\n" +   "Name: " + record.givenNames + " " + record.surname + "\n" + " " +  "\n" +  "Nationality: " + record.nationality + " " +  "\n" +  "Expire Date: " +record.expirationDate + " "  +  "\n" +  "Gender: " +record.sex + " "
                    System.out.println("Name: " + record.givenNames + " " + record.surname);

                    NAME_GLOBAL =  record.givenNames
                    MRZ_GLOBAL = text;
                    findViewById<ImageView>(R.id.machineImage).visibility=View.GONE
                }

                withContext(Dispatchers.Main){
                        findViewById<TextView>(R.id.textPassoert).setText(text)


                }

               
             //   _stateSelectService.value = stateSelectService.value.copy(success = mrz)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mScanner?.close()
            if (mrz.trim { it <= ' ' }.length == 0) {
                //    tvMrz.setText(R.string.error_mrzscanner_nomrz)
            } else {
                //   tvMrz.setText(mrz.replace('\r', '\n'))
            }
        } else {
            //  tvMrz.setText(R.string.error_mrzscanner_conn)
        }

     //  delay(100)
     //  readContinueLoop()
    }

}