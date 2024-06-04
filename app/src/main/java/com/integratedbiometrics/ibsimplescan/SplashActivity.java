package com.integratedbiometrics.ibsimplescan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.s10.test.UsbSession;
import com.telpo.tps550.api.fingerprint.FingerPrint;


public class SplashActivity extends Activity {
    Handler handler = new Handler();
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FingerPrint.fingerPrintPower(1);
        UsbSession usbSession = new UsbSession();
        usbSession.fingerPower(1);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            Intent intent = new Intent(getApplicationContext(),SimpleScanActivity.class);
                //intent.putExtra("start",true);
                    startActivity(intent);
                    finish();
            }
        },1000);
    }
}
