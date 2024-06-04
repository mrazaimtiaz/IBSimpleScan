package com.integratedbiometrics.ibsimplescan

import android.annotation.SuppressLint
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.vignesh.nfccardreader.NfcCardManager
import com.vignesh.nfccardreader.NfcCardReader

class NfcActivity : AppCompatActivity() {

    private var nfcCardManager: NfcCardManager? = null
    private var nfcCardReader: NfcCardReader? = null

    lateinit var  textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)

        getSupportActionBar()?.hide()

        findViewById<View>(R.id.back).setOnClickListener { finish() }

        textView = findViewById(R.id.textNfc)


        nfcCardManager = NfcCardManager(this)
        nfcCardReader = NfcCardReader()
    }

    override fun onPause() {
        super.onPause()
        // textView?.setText(null)
        nfcCardManager!!.disableDispatch()
    }

    override fun onResume() {
        super.onResume()
        nfcCardManager!!.enableDispatch()
    }

    @SuppressLint("SetTextI18n")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            Log.d("TAG", "onNewIntent:1 " + bytesToHexString(tag.id))
            Log.d("TAG", "onNewIntent:2 $tag")
            val cardResponse = nfcCardReader?.readCard(tag)
            Log.d(
                "TAG",
                "onNewIntent: " + cardResponse + "---" + cardResponse?.emvCard?.aid + "--" +  cardResponse?.emvCard?.atrDescription
            )
            if (cardResponse != null && cardResponse.emvCard != null) {
              textView.text = cardResponse.emvCard.cardNumber + " " +
                       cardResponse.emvCard.expireDate+
                       cardResponse.emvCard.holderFirstname+
                       cardResponse.emvCard.holderLastname
            }else{
               textView.text = bytesToHexString(tag.id)
            }

        }
    }

    private fun bytesToHexString(bytes: ByteArray): String {
        val builder = StringBuilder()
        for (b in bytes) {
            builder.append(String.format("%02X", b))
        }
        return builder.toString()
    }
}