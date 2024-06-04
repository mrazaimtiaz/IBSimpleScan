package com.integratedbiometrics.ibsimplescan.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UsbAttachedReceiver extends BroadcastReceiver {

    public static final String ACTION_MODULE_ATTACHED = "com.elyctis.idtabsuite.MODULE_ATTACHED";
    public static final String ACTION_MODULE_DETACHED = "com.elyctis.idtabsuite.MODULE_DETACHED";
    public static final String ACTION_MODULE_FORCE_REFRESH = "com.elyctis.idtabsuite.MODULE_FORCE_REFRESH";

    private final String ACTION_USB_DEVICE_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private final String ACTION_USB_DEVICE_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private static final Object sLock = new Object();
    private static boolean sAttached = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        synchronized (sLock) {
            if (ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
                if(!sAttached) {
                    sAttached = true;
                    Intent i = new Intent(ACTION_MODULE_ATTACHED);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    context.sendBroadcast(i);
                }
            } else if (ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
                if(sAttached) {
                    sAttached = false;
                    Intent i = new Intent(ACTION_MODULE_DETACHED);
                    context.sendBroadcast(i);
                }
            }
        }
    }
}
