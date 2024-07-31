package com.integratedbiometrics.ibsimplescan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.elyctis.idtabsdk.io.Io
import com.elyctis.idtabsdk.mrz.MrzScanner
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity
import com.example.s10.test.UsbSession
import com.integratedbiometrics.ibsimplescan.utils.*
import com.integratedbiometrics.ibsimplescan.utils.Constants.MRZ_GLOBAL
import com.integratedbiometrics.ibsimplescan.utils.Constants.NAME_GLOBAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ScheduledExecutorService

var copyText = ""

private var clipboardManager: ClipboardManager? = null
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

    private fun copyTextToClipboard(text: String) {
        // Create a ClipData object with the text to be copied
        val clipData = ClipData.newPlainText("label", text)

        // Set the ClipData to the ClipboardManager
        clipboardManager!!.setPrimaryClip(clipData)

        // Show a toast message to inform the user
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport)

            getSupportActionBar()?.hide()

        findViewById<View>(R.id.back).setOnClickListener { finish() }


        findViewById<View>(R.id.search).setOnClickListener {
            if(     findViewById<LinearLayout>(R.id.layoutSearch).visibility == View.VISIBLE){
                findViewById<LinearLayout>(R.id.layoutSearch).visibility =  View.GONE

            }else{
                findViewById<LinearLayout>(R.id.layoutSearch).visibility =  View.VISIBLE

            }
        }
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?;

        findViewById<Button>(R.id.CopyBtn).setOnClickListener {
            findViewById<LinearLayout>(R.id.passportLayout).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.dummyLayout).visibility = View.GONE


            findViewById<TextView>(R.id.name_value).setText(  findViewById<TextView>(R.id.name_value_visa).text)

            findViewById<TextView>(R.id.nationality_value).setText(findViewById<TextView>(R.id.nationality_value_visa).text)

          //  Constants.nationalityName =  record.nationality

            findViewById<TextView>(R.id.expiry_value).setText(findViewById<TextView>(R.id.expiry_value_visa).text)
            findViewById<TextView>(R.id.dob_value).setText(findViewById<TextView>(R.id.dob_value_visa).text)
            findViewById<TextView>(R.id.gender_value).setText(findViewById<TextView>(R.id.gender_value_visa).text)

            findViewById<TextView>(R.id.document_value).setText(findViewById<TextView>(R.id.document_value_visa).text)




            Constants.passportName =  findViewById<TextView>(R.id.name_value).text.toString()
            Constants.nationalityName =  findViewById<TextView>(R.id.nationality_value).text.toString()
            Constants.expirationName =   findViewById<TextView>(R.id.expiry_value).text.toString()


            Constants.dobValue =      findViewById<TextView>(R.id.dob_value).text.toString()

            Constants.passportNumber =findViewById<TextView>(R.id.document_value).text.toString()

            // copyTextToClipboard(copyText);

        }

        findViewById<Button>(R.id.search_button).setOnClickListener {
        var searchText =    findViewById<EditText>(R.id.search_edit_text).text.toString()

            if(searchText == sampleUserData.passportNumber || searchText == sampleUserData.moireference){

                setUpUserData(sampleUserData)
                setUpOnlyPassportNumber(sampleUserData.passportNumber)
                findViewById<TextView>(R.id.textPassoert).visibility = View.GONE
            }else if(searchText == sampleUserDataTwo.passportNumber || searchText == sampleUserDataTwo.moireference){
                setUpUserData(sampleUserDataTwo)
                setUpOnlyPassportNumber(sampleUserDataTwo.passportNumber)
                findViewById<TextView>(R.id.textPassoert).visibility = View.GONE
            }else if(searchText == sampleUserDataThree.passportNumber || searchText == sampleUserDataThree.moireference){
                setUpUserData(sampleUserDataThree)
                setUpOnlyPassportNumber(sampleUserDataThree.passportNumber)
                findViewById<TextView>(R.id.textPassoert).visibility = View.GONE
            }else if(searchText ==  sampleUserDataFour.moireference  || searchText ==  sampleUserDataFour.passportNumber){
                setUpUserData(sampleUserDataFour)
                setUpOnlyPassportNumber(sampleUserDataFour.passportNumber)

                findViewById<TextView>(R.id.gender_value_visa).setText("Female")

                findViewById<TextView>(R.id.textPassoert).visibility = View.GONE
            }

            else{
                showNotFoundMsg("No data Found")
            }

        }


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

    fun showNotFoundMsg(text: String){

        findViewById<Button>(R.id.CopyBtn).visibility = View.GONE
        findViewById<LinearLayout>(R.id.visaLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.passportLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.layoutSearch).visibility = View.VISIBLE
        findViewById<TextView>(R.id.searchResult).visibility = View.VISIBLE
        findViewById<TextView>(R.id.searchResult).text = text
    }

    fun setUpOnlyPassportNumber( text: String){
        findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.passportLayout).visibility = View.GONE
        findViewById<LinearLayout>(R.id.dummyLayout).visibility = View.VISIBLE
        findViewById<TextView>(R.id.name_value).setText("")

        findViewById<TextView>(R.id.visa_number_value).setText("")



        findViewById<TextView>(R.id.nationality_value).setText("")

        findViewById<TextView>(R.id.expiry_value).setText("")

        findViewById<TextView>(R.id.gender_value).setText("")
        findViewById<TextView>(R.id.document_value).setText("")
        findViewById<TextView>(R.id.dob_value).setText("")


      //  findViewById<TextView>(R.id.document_value).setText(text)




        findViewById<LinearLayout>(R.id.layoutSearch).visibility = View.GONE
        findViewById<TextView>(R.id.name_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.name_value_visa).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.searchResult).visibility = View.GONE


        findViewById<TextView>(R.id.nationality_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.nationality_value_visa).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.expiry_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.expiry_value_visa).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.gender_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.gender_value_visa).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.document_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.document_value_visa).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.dob_value_visa).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.dob_value_visa).setBackgroundColor(Color.TRANSPARENT)


        findViewById<TextView>(R.id.name_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.name_value).setBackgroundColor(Color.TRANSPARENT)


        findViewById<TextView>(R.id.nationality_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.nationality_value).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.expiry_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.expiry_value).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.gender_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.gender_value).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.document_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.document_value).setBackgroundColor(Color.TRANSPARENT)

        findViewById<TextView>(R.id.dob_value).setTextColor(Color.BLACK)
        findViewById<TextView>(R.id.dob_value).setBackgroundColor(Color.TRANSPARENT)



        //   findViewById<LinearLayout>(R.id.visaLayout).setBackgroundColor(Color.GREEN)
        //  updateTextColor(  findViewById<LinearLayout>(R.id.visaLayout), Color.WHITE)

        // Compare and update text colors
        //   updateTextColors()
//                            if (isMatch) {
//
//                            } else {
//                                visaLayout.setBackgroundColor(Color.RED)
//                                updateTextColor(visaLayout, Color.WHITE)
//                            }
    }

    fun setUpUserData( tempUserData: UserData){
        findViewById<Button>(R.id.CopyBtn).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.passportLayout).visibility = View.VISIBLE
        findViewById<TextView>(R.id.name_value_visa).setText(tempUserData.passportName)
        findViewById<TextView>(R.id.moi_value_visa).setText(tempUserData.moireference)
        findViewById<TextView>(R.id.passport_type_value_visa).setText(tempUserData.passportType)

        findViewById<TextView>(R.id.visa_number_value_visa).setText(tempUserData.visaNumber)
        findViewById<TextView>(R.id.visa_type_value_visa).setText(tempUserData.visaType)
        findViewById<TextView>(R.id.visa_expiry_value_visa).setText(tempUserData.visaExpiryDate)
        findViewById<TextView>(R.id.date_of_issue_value_visa).setText(tempUserData.visaIssueDate)



        findViewById<TextView>(R.id.nationality_value_visa).setText(tempUserData.nationalityName)

        findViewById<TextView>(R.id.expiry_value_visa).setText(tempUserData.expirationName)

        findViewById<TextView>(R.id.gender_value_visa).setText("Male")
        findViewById<TextView>(R.id.document_value_visa).setText(tempUserData.passportNumber)
        findViewById<TextView>(R.id.dob_value_visa).setText(tempUserData.dobValue)

        //   findViewById<LinearLayout>(R.id.visaLayout).setBackgroundColor(Color.GREEN)
        //  updateTextColor(  findViewById<LinearLayout>(R.id.visaLayout), Color.WHITE)

        // Compare and update text colors
        //   updateTextColors()
//                            if (isMatch) {
//
//                            } else {
//                                visaLayout.setBackgroundColor(Color.RED)
//                                updateTextColor(visaLayout, Color.WHITE)
//                            }
    }


    val matchingColor = Color.parseColor("#006400") // Dark green color
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
                    findViewById<TextView>(R.id.textPassoert).visibility =View.VISIBLE
                    findViewById<TextView>(R.id.textPassoert).setText(text)
                    findViewById<LinearLayout>(R.id.dummyLayout).visibility = View.GONE

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
                        findViewById<TextView>(R.id.textPassoert).visibility =View.GONE
                        findViewById<LinearLayout>(R.id.passportLayout).visibility = View.VISIBLE

                        findViewById<LinearLayout>(R.id.dummyLayout).visibility = View.GONE



                        findViewById<TextView>(R.id.name_value).setText( record.givenNames + " " + record.surname)

                        Constants.passportName =  record.givenNames
                        findViewById<TextView>(R.id.nationality_value).setText(record.nationality)

                        Constants.nationalityName =  record.nationality
                        findViewById<TextView>(R.id.expiry_value).setText(record.expirationDate.day.toString() + "/" + record.expirationDate.month.toString() + "/"  +record.expirationDate.year.toString())
                        Constants.expirationName =  record.expirationDate.day.toString() + "/" + record.expirationDate.month.toString() + "/"  +record.expirationDate.year.toString()


                        findViewById<TextView>(R.id.dob_value).setText(record.dateOfBirth.day.toString() + "/" + record.dateOfBirth.month.toString() + "/"  +record.dateOfBirth.year.toString())
                        Constants.dobValue =  record.dateOfBirth.day.toString() + "/" + record.dateOfBirth.month.toString() + "/"  +record.dateOfBirth.year.toString()
                        findViewById<TextView>(R.id.gender_value).setText(record.sex.name)
                        findViewById<TextView>(R.id.document_value).setText(record.documentNumber)
                        Constants.passportNumber = record.documentNumber


                        copyText =  "Name: " + record.givenNames + "\n" +  "Nationality: " + record.nationality + "\n" + "Date of Birth: "+ record.dateOfBirth.day.toString() + "/" + record.dateOfBirth.month.toString() + "/"  +record.dateOfBirth.year.toString() + "\n" + "Gender: " + record.sex.name + "\n" + "Passport Number: " + record.documentNumber






                        if(record.documentNumber == sampleUserData.passportNumber || record.documentNumber == sampleUserDataTwo.passportNumber || record.documentNumber == sampleUserDataFour.passportNumber){
                            findViewById<LinearLayout>(R.id.layoutSearch).visibility = View.GONE
                            findViewById<TextView>(R.id.name_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.name_value_visa).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.searchResult).visibility = View.GONE


                            findViewById<TextView>(R.id.nationality_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.nationality_value_visa).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.expiry_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.expiry_value_visa).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.gender_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.gender_value_visa).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.document_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.document_value_visa).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.dob_value_visa).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.dob_value_visa).setBackgroundColor(lightGreenColor)


                            findViewById<TextView>(R.id.name_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.name_value).setBackgroundColor(lightGreenColor)


                            findViewById<TextView>(R.id.nationality_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.nationality_value).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.expiry_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.expiry_value).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.gender_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.gender_value).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.document_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.document_value).setBackgroundColor(lightGreenColor)

                            findViewById<TextView>(R.id.dob_value).setTextColor(matchingColor)
                            findViewById<TextView>(R.id.dob_value).setBackgroundColor(lightGreenColor)


                        }
                        if(record.documentNumber == sampleUserData.passportNumber){
                            findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE

                            setUpUserData(sampleUserData)
//                            findViewById<TextView>(R.id.name_value_visa).setText(sampleUserData.passportName)
//                            findViewById<TextView>(R.id.moi_value_visa).setText(sampleUserData.moireference)
//                            findViewById<TextView>(R.id.passport_type_value_visa).setText(sampleUserData.passportType)
//
//                            findViewById<TextView>(R.id.visa_number_value_visa).setText(sampleUserData.visaNumber)
//                            findViewById<TextView>(R.id.visa_type_value_visa).setText(sampleUserData.visaType)
//                            findViewById<TextView>(R.id.visa_expiry_value_visa).setText(sampleUserData.visaExpiryDate)
//                            findViewById<TextView>(R.id.date_of_issue_value_visa).setText(sampleUserData.visaIssueDate)
//
//
//
//                            findViewById<TextView>(R.id.nationality_value_visa).setText(sampleUserData.nationalityName)
//
//                            findViewById<TextView>(R.id.expiry_value_visa).setText(sampleUserData.expirationName)
//
//                            findViewById<TextView>(R.id.gender_value_visa).setText("Male")
//                            findViewById<TextView>(R.id.document_value_visa).setText(sampleUserData.passportNumber)
//                            findViewById<TextView>(R.id.dob_value_visa).setText(sampleUserData.dobValue)



                        }else  if(record.documentNumber == sampleUserDataFour.passportNumber){
                            findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE

                            setUpUserData(sampleUserDataFour)

                            findViewById<TextView>(R.id.gender_value_visa).setText("Female")
//                            findViewById<TextView>(R.id.name_value_visa).setText(sampleUserData.passportName)
//                            findViewById<TextView>(R.id.moi_value_visa).setText(sampleUserData.moireference)
//                            findViewById<TextView>(R.id.passport_type_value_visa).setText(sampleUserData.passportType)
//
//                            findViewById<TextView>(R.id.visa_number_value_visa).setText(sampleUserData.visaNumber)
//                            findViewById<TextView>(R.id.visa_type_value_visa).setText(sampleUserData.visaType)
//                            findViewById<TextView>(R.id.visa_expiry_value_visa).setText(sampleUserData.visaExpiryDate)
//                            findViewById<TextView>(R.id.date_of_issue_value_visa).setText(sampleUserData.visaIssueDate)
//
//
//
//                            findViewById<TextView>(R.id.nationality_value_visa).setText(sampleUserData.nationalityName)
//
//                            findViewById<TextView>(R.id.expiry_value_visa).setText(sampleUserData.expirationName)
//
//                            findViewById<TextView>(R.id.gender_value_visa).setText("Male")
//                            findViewById<TextView>(R.id.document_value_visa).setText(sampleUserData.passportNumber)
//                            findViewById<TextView>(R.id.dob_value_visa).setText(sampleUserData.dobValue)



                        }else if(record.documentNumber == sampleUserDataTwo.passportNumber){

                            findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE
                            setUpUserData(sampleUserDataTwo)
//                            findViewById<LinearLayout>(R.id.visaLayout).visibility = View.VISIBLE
//                            findViewById<TextView>(R.id.name_value_visa).setText(sampleUserDataTwo.passportName)
//                            findViewById<TextView>(R.id.moi_value_visa).setText(sampleUserDataTwo.moireference)
//                            findViewById<TextView>(R.id.passport_type_value_visa).setText(sampleUserDataTwo.passportType)
//
//                            findViewById<TextView>(R.id.visa_number_value_visa).setText(sampleUserDataTwo.visaNumber)
//                            findViewById<TextView>(R.id.visa_type_value_visa).setText(sampleUserDataTwo.visaType)
//                            findViewById<TextView>(R.id.visa_expiry_value_visa).setText(sampleUserDataTwo.visaExpiryDate)
//                            findViewById<TextView>(R.id.date_of_issue_value_visa).setText(sampleUserDataTwo.visaIssueDate)
//
//
//
//
//
//                            findViewById<TextView>(R.id.nationality_value_visa).setText(sampleUserDataTwo.nationalityName)
//
//                            findViewById<TextView>(R.id.expiry_value_visa).setText(sampleUserDataTwo.expirationName)
//                            findViewById<TextView>(R.id.document_value_visa).setText(sampleUserDataTwo.passportNumber)
//                            findViewById<TextView>(R.id.dob_value_visa).setText(sampleUserDataTwo.dobValue)
//
//                            findViewById<TextView>(R.id.gender_value_visa).setText("Male")






                            findViewById<TextView>(R.id.expiry_value_visa).setTextColor(darkRedColor)
                            findViewById<TextView>(R.id.expiry_value_visa).setBackgroundColor(lightRedColor)

                            findViewById<TextView>(R.id.expiry_value).setTextColor(darkRedColor)
                            findViewById<TextView>(R.id.expiry_value).setBackgroundColor(lightRedColor)


                          //  findViewById<TextView>(R.id.document_value).setText("28/8/32")



                        }else{

                        showNotFoundMsg("No match Found for current Passport ")
                        }

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

                    //    findViewById<ImageView>(R.id.machineImage).visibility=View.GONE

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

    val lightGreenColor = Color.parseColor("#d0e8a6") // Dark green color


    val lightRedColor = Color.parseColor("#f9cfcf") // Dark green color


    val darkRedColor = Color.parseColor("#9c0c0c") // Dark green color


    private fun updateTextColors() {


        // Define the color for matching data
        val matchingColor = Color.parseColor("#006400") // Dark green color





        // Update Visa text color
     //   if (isMatch) {
            setTextColor(findViewById(R.id.visaLayout), matchingColor)
            setTextColor(findViewById(R.id.passportLayout), matchingColor)
//        } else {
//            // Reset text color to default (black) for non-matching data
//            setTextColor(findViewById(R.id.visaLayout), Color.BLACK)
//            setTextColor(findViewById(R.id.passportLayout), Color.BLACK)
//        }
    }

    private fun setTextColor(layout: LinearLayout, color: Int) {
        for (i in 0 until layout.childCount) {
            val view = layout.getChildAt(i)
            if (view is TextView) {
              //  if(view.text != "28/8/32" && view.text != sampleUserDataTwo.expirationName){

                    view.setTextColor(color)
                    view.setBackgroundColor(lightGreenColor)
              //  }
            }
        }
    }

}