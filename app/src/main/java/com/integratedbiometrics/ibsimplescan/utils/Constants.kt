package com.integratedbiometrics.ibsimplescan.utils

import android.graphics.Bitmap

object Constants {
//http://10.99.55.22/qtouch_api_Centralized/Api/GetBranches
    //Language

     var NAME_GLOBAL = ""

     var MRZ_GLOBAL = ""

     var featureCamera:
             ByteArray? = null


     var picture:
             Bitmap? = null
     var fingerFeature:
             List<ByteArray>? = null

     var scanByte:
             List<Bitmap>? = null
     var cameraByte:
             ByteArray? = null

     var passportName = ""
     var nationalityName = ""
     var dobValue = ""
     var passportNumber = ""
     var expirationName = ""

}