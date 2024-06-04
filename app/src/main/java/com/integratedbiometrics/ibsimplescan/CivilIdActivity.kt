package com.integratedbiometrics.ibsimplescan

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.integratedbiometrics.ibsimplescan.pacicardlibrary.PaciCardReaderAbstract
import com.integratedbiometrics.ibsimplescan.pacicardlibrary.PaciCardReaderMAV3


import com.telpo.tps550.api.fingerprint.FingerPrint
import com.telpo.tps550.api.reader.SmartCardReader
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class CivilIdActivity : AppCompatActivity() {


    lateinit var progressBar: ProgressBar
    lateinit var textCivilId: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_civil_id)

        getSupportActionBar()?.hide()
        val mUsbManager = getSystemService(USB_SERVICE) as UsbManager

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        textCivilId = findViewById<TextView>(R.id.textCivilId)


        findViewById<ImageView>(R.id.back).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.clearBtn).setOnClickListener {
            cardInserted = false;
            findViewById<Button>(R.id.clearBtn).visibility = View.GONE
            textCivilId.setText("")
            CoroutineScope(Dispatchers.IO).launch {
                autoDetectCivilId()

            }
        }

        settingReader(this)


    }

    var reader: SmartCardReader? = null

    fun settingReader(context: Context) {
        reader = SmartCardReader(context)

        CoroutineScope(Dispatchers.IO).launch {
            //   _stateMain.value = _stateMain.value.copy(isLoadingCivilId = true, showToast = "")
            //loading true --my
            withContext(Dispatchers.Main){
                progressBar.visibility = View.VISIBLE

            }
            delay(3000)
            reader?.open(1)
            //loading false --my
            withContext(Dispatchers.Main){
                progressBar.visibility = View.GONE

            }
            // _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false, showToast = "")
            autoDetectCivilId()
        }
    }


    var cardInserted = false
   suspend fun autoDetectCivilId() {

        try {
//            d("TAG", "autoDetectCivilId: ${cardInserted} readerPresent: ${reader.iccPowerOn()}")

            reader?.iccPowerOff()

            if (!cardInserted) {
                if (reader?.iccPowerOn() == true) {
                    Log.d("TAG", "autoDetectCivilId: cardinsert")

                    readData()

                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(500)
                        autoDetectCivilId()
                    }
                }
            } else {
                if (reader?.iccPowerOn() == false) {
                    Log.d("TAG", "autoDetectCivilId: cardremoved")
                    cardInserted = false
                }
                CoroutineScope(Dispatchers.IO).launch {
                    delay(500)
                    autoDetectCivilId()
                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            CoroutineScope(Dispatchers.IO).launch {
                delay(500)
                autoDetectCivilId()
            }
        }


    }


    suspend fun readData(civilIdMatch: String = "", ) {
        //loading true -- my
        withContext(Dispatchers.Main){
          progressBar.visibility = View.VISIBLE

        }
       //if (reader?.iccPowerOn() == true) {
           val ReaderHandler: ConcurrentHashMap<String?, PaciCardReaderAbstract?> =
               ConcurrentHashMap<String?, PaciCardReaderAbstract?>()
           val paci = PaciCardReaderMAV3(
               "true" == System.getProperty(
                   "sun.security.smartcardio.t0GetResponse", "true"
               ), reader
           )
           CoroutineScope(Dispatchers.IO).launch {
               var civilidText: String = ""
               var serialNoText: String = ""
               var fullNameText: String = ""
               var firstNameText: String = ""
               var secondNameText: String = ""
               var thirdNameText: String = ""
               var fourNameText: String = ""
               var fullNameArText: String = ""
               var firstNameArText: String = ""
               var secondNameArText: String = ""
               var thirdNameArText: String = ""
               var fourNameArText: String = ""
               var fullAddressText: String = ""
               var genderText: String = ""
               var bloodGroupText: String = ""
               var passportNoText: String = ""
               var occupationText: String = ""
               var dobText: String = ""
               var nationalityText: String = ""
               var expiryText: String = ""
               var tel1Text: String = ""
               var tel2Text: String = ""
               var emailText: String = ""


               var text = "";
               try {

                   try {
                       civilidText = paci!!.GetData("", "CIVIL-NO")
                       Log.d("TAG", "readData: paci $civilidText");
                   } catch (e: java.lang.Exception) {
                       Log.d("TAG", "readData: paci2 ${e.toString()}");
                       e.printStackTrace()
                   }
                   try {
                       firstNameText = paci.GetData("1", "LATIN-NAME-1")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       secondNameText = paci.GetData("1", "LATIN-NAME-2")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       thirdNameText = paci.GetData("1", "LATIN-NAME-3")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       firstNameArText = paci!!.GetData("", "ARABIC-NAME-1")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       secondNameArText = paci.GetData("", "ARABIC-NAME-2")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       thirdNameArText = paci.GetData("", "ARABIC-NAME-3")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       fourNameArText = paci.GetData("", "ARABIC-NAME-4")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       genderText = paci.GetData("1", "SEX-LATIN-TEXT")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       tel1Text = paci.GetData("1", "TEL-1")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
//                try {
//                    nationalityText = paci.GetData("1", "NATIONALITY-LATIN-ALPHA-CODE")
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//                try {
//                    tel2Text = paci.GetData("1", "TEL-2")
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//                try {
//                    emailText = paci.GetData("1", "E-MAIL-ADDRESS")
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
                   try {
                       dobText = paci.GetData("1", "BIRTH-DATE")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   try {
                       expiryText = paci.GetData("1", "CARD-EXPIRY-DATE")
                   } catch (e: java.lang.Exception) {
                       e.printStackTrace()
                   }
                   fullNameArText =
                       "$firstNameArText $secondNameArText $thirdNameArText $fourNameArText"
                   fullNameText = "$firstNameText $secondNameText $thirdNameText $fourNameText"

                   try {
                       expiryText = expiryText.substring(0, 4) + "-" + expiryText.substring(
                           4, 6
                       ) + "-" + expiryText.substring(6, 8)
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }

                   try {
                       dobText = dobText.substring(0, 4) + "-" + dobText.substring(
                           4, 6
                       ) + "-" + dobText.substring(6, 8)
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }
                   try {

                       passportNoText = paci.GetData("1", "LATIN-NAME-4")
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }

                   try {

//                       fullAddressText +=  "Building No. " + paci.GetData("1", "BUILDING-PLOT-NO") + " "
                    //   fullAddressText +=  paci.GetData("1", "BUILDING-PLOT-NO") + " "
//fullAddressText += "F No. " +  paci.GetData("1", "FLOOR-NO")+ " "
 fullAddressText += "Block No. " + paci.GetData("1", "BLOCK-NO")+ " "
 fullAddressText +=  paci.GetData("1", "STREET-NAME")+ " "
 fullAddressText +=  paci.GetData("1", "DESTRICT")+ " "
fullAddressText +=  paci.GetData("1", "UNIT-TYPE")+ " "
fullAddressText +=  paci.GetData("1", "UNIT-NO")+ " "
//  fullAddressText +=  paci.GetData("1", "ADDRESS-UNIQUE-KEY")+ " "
                   } catch (e: Exception) {
                       e.printStackTrace()
                   }

//                   try {
//
//                       serialNoText = paci.GetData("1", "CARD-SERIAL-NO") //not actual serial number
//                   } catch (e: Exception) {
//                       e.printStackTrace()
//                   }

                   //set Text-- my
                   withContext(Dispatchers.Main) {
                       progressBar.visibility = View.GONE

                   }

                   var showText = "Civil Id: $civilidText"

                   //showText = showText + "\n  Serial No: $serialNoText "
                   showText = showText + "\n  Full Name No: $fullNameText "
                   showText = showText + "\n  Full Name Ar: $fullNameArText "
                   showText = showText + "\n  Full Address: $fullAddressText "
                   showText = showText + "\n  Gender: $genderText "
                   showText = showText + "\n  Tel1: $tel1Text "
                   //  showText = showText + "\n  Blood Group: $bloodGroupText "
                   showText = showText + "\n  Passport No: $passportNoText "
                   showText = showText + "\n  Expiry Date: $expiryText "
                   showText = showText + "\n  Date of Birth: $dobText "

                   withContext(Dispatchers.Main) {
                       if (civilidText.isNotEmpty()) {
                           textCivilId.setText(showText)
                           cardInserted = true;
                           findViewById<Button>(R.id.clearBtn).visibility = View.VISIBLE

                       }

                   }

//                onEvent(MyEvent.CivilIdChanged(civilidText))
//                onEvent(MyEvent.SerialNoChanged(serialNoText))
//                onEvent(MyEvent.FirstNameChanged(firstNameText))
//                onEvent(MyEvent.SecondNameChanged(secondNameText))
//                onEvent(MyEvent.ThirdNameChanged(thirdNameText))
//                onEvent(MyEvent.FullNameChanged(fullNameText))
//                onEvent(MyEvent.FirstNameArChanged(firstNameArText))
//                onEvent(MyEvent.SecondNameArChanged(secondNameArText))
//                onEvent(MyEvent.ThirdNameArChanged(thirdNameArText))
//                onEvent(MyEvent.FullNameArChanged(fullNameArText))
//                onEvent(MyEvent.FullAddressChanged(fullAddressText))
//                onEvent(MyEvent.GenderChanged(genderText))
//                onEvent(MyEvent.BloodGroupChanged(bloodGroupText))
//                onEvent(MyEvent.PassportNoChanged(passportNoText))
//                onEvent(MyEvent.Tel1Changed(tel1Text))
//                onEvent(MyEvent.NationalityChanged(nationalityText))
//                onEvent(MyEvent.Tel2Changed(tel2Text))
//                onEvent(MyEvent.EmailChanged(emailText))
//                onEvent(MyEvent.DOBChanged(dobText))
//                onEvent(MyEvent.ExpiryDateChanged(expiryText))


              //     autoDetectCivilId()
                   withContext(Dispatchers.Main) {
                       progressBar.visibility = View.GONE

                   }

               } catch (e: Exception) {
                   withContext(Dispatchers.Main) {
                       progressBar.visibility = View.GONE

                   }

                   withContext(Dispatchers.Main) {
                       textCivilId.setText(e.toString())

                   }

                   e.printStackTrace()
               }
               /*_stateMain.value = _stateMain.value.copy(
                isLoadingCivilId = false,
                fingerPrintPage = true,
                civilIdPage = false,
                signaturePage = false
            )*/  //test

//        } else {
//            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false, showToast = "No CivilID Found \n لم يتم العثور على بطاقة هوية مدنية ")
//        }

           }
     //  }

    }

}