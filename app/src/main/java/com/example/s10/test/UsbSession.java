package com.example.s10.test;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UsbSession {
   private static final String TAG = "PowerUtil";

   static {
      System.loadLibrary("usbtest");
   }

   private native int switch_psp_on();

   private native int switch_psp_off();

   private native int switch_iris_on();

   private native int switch_iris_off();

   private native int switch_usb_on();

   private native int switch_usb_off();

   private native int switch_net_led_on();

   private native int switch_net_led_off();

   private native int switch_power_led_on();

   private native int switch_power_led_off();

   private native int switch_dc_led_on();

   private native int switch_dc_led_off();

   private native int switch_vbus_on();

   private native int switch_vbus_off();

   private native int switch_ethernet_on();

   private native int switch_ethernet_off();

   private native int switch_fingerpower_on();

   private native int switch_fingerpower_off();

   private native int switch_adb();

   private native int switch_otg();

   private native int switch_camerapower_on();

   private native int switch_camerapower_off();

   private native int switch_cameraledpower_on();

   private native int switch_cameraledpower_off();

   public synchronized int brightnessAdjustment(Context mContext, int value) throws Exception {
      Class<?> led = null;
      Method method = null;
      Object obj = null;
      int result = 0;

      try {
         led = Class.forName("com.common.sdk.led.LEDServiceManager");
      } catch (ClassNotFoundException var10) {
         var10.printStackTrace();
         throw new Exception();
      }

      obj = mContext.getSystemService("LED");

      try {
         method = led.getMethod("brightnessAdjustment", Integer.TYPE);
      } catch (NoSuchMethodException var9) {
         var9.printStackTrace();
         throw new Exception();
      }

      try {
         result = (Integer)method.invoke(obj, value);
      } catch (IllegalArgumentException var11) {
         var11.printStackTrace();
         throw new Exception();
      } catch (IllegalAccessException var12) {
         var12.printStackTrace();
         throw new Exception();
      } catch (InvocationTargetException var13) {
         var13.printStackTrace();
         Exception targetExp = (Exception)var13.getTargetException();
         if (targetExp instanceof Exception) {
            throw (Exception)targetExp;
         }
      }

      return result;
   }

   public int pspPower(int mode) {
      Log.w("PowerUtil", "pspPower==================");
      return mode == 1 ? this.switch_psp_on() : this.switch_psp_off();
   }

   public int irisPower(int mode) {
      Log.w("PowerUtil", "irisPower==================");
      return mode == 1 ? this.switch_iris_on() : this.switch_iris_off();
   }

   public int usbPower(int mode) {
      Log.w("PowerUtil", "usbPower==================");
      return mode == 1 ? this.switch_usb_on() : this.switch_usb_off();
   }

   public int netLedPower(int mode) {
      Log.w("PowerUtil", "netLedPower==================");
      return mode == 1 ? this.switch_net_led_on() : this.switch_net_led_off();
   }

   public int powerLedPower(int mode) {
      Log.w("PowerUtil", "powerLedPower==================");
      return mode == 1 ? this.switch_power_led_on() : this.switch_power_led_off();
   }

   public int dcLedPower(int mode) {
      Log.w("PowerUtil", "dcLedPower==================");
      return mode == 1 ? this.switch_dc_led_on() : this.switch_dc_led_off();
   }

   public int vbusPower(int mode) {
      Log.w("PowerUtil", "vbusPower==================");
      return mode == 1 ? this.switch_vbus_on() : this.switch_vbus_off();
   }

   public int fingerPower(int mode) {
      Log.w("PowerUtil", "fingerPower==================");
      return mode == 1 ? this.switch_fingerpower_on() : this.switch_fingerpower_off();
   }

   public int cameraPower(int mode) {
      Log.w("PowerUtil", "cameraPower==================");
      return mode == 1 ? this.switch_camerapower_on() : this.switch_camerapower_off();
   }

   public int cameraLedPower(int mode) {
      Log.w("PowerUtil", "cameraLedPower==================");
      return mode == 1 ? this.switch_cameraledpower_on() : this.switch_cameraledpower_on();
   }

   public int changeUsbMode(int mode) {
      Log.w("PowerUtil", "changeUsbMode==================");
      return mode == 1 ? this.switch_adb() : this.switch_otg();
   }
}
