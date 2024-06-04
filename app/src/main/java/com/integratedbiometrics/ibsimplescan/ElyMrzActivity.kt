package com.integratedbiometrics.ibsimplescan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.elyctis.idtabsdk.io.Io
import com.elyctis.idtabsdk.mrz.MrzScanner
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity
import com.integratedbiometrics.ibsimplescan.utils.UsbAttachedReceiver


class ElyMrzActivity : AppCompatActivity() {
    private var tvMrz: TextView? = null
    var TAG = "ElyMrzActivity"
    private var mScanner: MrzScanner? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mPendingIntent: PendingIntent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mrz)
        setupActionBar()
        tvMrz = findViewById(R.id.tvMrz) as TextView?
        mScanner = MrzScanner(getApplicationContext(), null)

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

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar: androidx.appcompat.app.ActionBar? = getSupportActionBar()
        actionBar?.setDisplayHomeAsUpEnabled(true)
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

    override fun onStop() {
        super.onStop()

        // Power OFF MRZ Scanner
        Io.powerOffTopModule()
    }

    fun getInfo(view: View?) {
        if (mScanner!!.open()) {
            try {
                val info = """
                    Firmware version:		${mScanner!!.firmwareVersion}
                    NNA version:							${mScanner!!.nnaVersion}
                    Product Info:						${mScanner!!.productInfo}
                    Serial Number:					${mScanner!!.serialNumber}
                    """.trimIndent()
                tvMrz!!.text = info
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mScanner!!.close()
        } else {
            tvMrz?.setText("error_mrzscanner_conn")
        }
    }

    fun readMrz(view: View?) {
        if (mScanner!!.open()) {
            var mrz = ""
            try {
                mrz = mScanner!!.readMrz()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mScanner!!.close()
            if (mrz.trim { it <= ' ' }.length == 0) {
                tvMrz?.setText("error_mrzscanner_nomrz")
            } else {
                tvMrz!!.text = mrz.replace('\r', '\n')
            }
        } else {
            tvMrz?.setText("error_mrzscanner_conn")
        }
    }
}