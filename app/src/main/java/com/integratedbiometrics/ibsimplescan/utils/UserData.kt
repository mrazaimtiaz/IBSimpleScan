package com.integratedbiometrics.ibsimplescan.utils


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

@Entity(tableName = "user_data")
@TypeConverters(Converters::class) // Apply TypeConverters
data class UserData(
    @PrimaryKey val passportNumber: String = "",
    val featureCamera: ByteArray? = null,
    val fingerFeatureOne: ByteArray? = null,
    val fingerFeatureTwo: ByteArray? = null,
    val fingerFeatureThree: ByteArray? = null,
    val scanByteOne: ByteArray? = null, // Bitmap converted to ByteArray
    val scanByteTwo: ByteArray? = null, // Bitmap converted to ByteArray
    val scanByteThree: ByteArray? = null, // Bitmap converted to ByteArray
    val cameraByte: ByteArray? = null,
    val passportName: String = "",
    val nationalityName: String = "",
    val dobValue: String = "",
    val expirationName: String = "",
    val enrollmentTime: Long? =null,
    val visaNumber: String = "",
    val visaType: String = "",
    val visaExpiryDate: String = "",
    val visaIssueDate: String = "",
    val passportType: String = "",
    val moireference: String = "",
)

var sampleUserData = UserData(passportName = "MOHAMMAD JABER AHMED ALHASAN", passportNumber = "Q584558", expirationName = "2026-09-26", dobValue = "1982-09-03", nationalityName = "JORDAN", visaType = "Public sector Work Visa",visaExpiryDate = "2024-04-15", visaNumber = "257277005", passportType = "Normal", moireference = "359046593",visaIssueDate = "2024-01-16")

var sampleUserDataTwo = UserData(passportName = "ASAD SADIQ MUHAMMAD SADIQ", passportNumber = "KB5465612", expirationName = "2032-09-28", dobValue = "1993-10-03", nationalityName = "PAKISTAN", visaType = "Public sector Work Visa",visaExpiryDate = "2024-10-16", visaNumber = "259267002", passportType = "Normal", moireference = "324046843",visaIssueDate = "2024-06-17")

var sampleUserDataThree = UserData(passportName = "MOHAMMAD RAZA MOHAMMAD IMTIAZ", passportNumber = "KM9896282", expirationName = "2027-11-20", dobValue = "1997-09-26", nationalityName = "PAKISTAN", visaType = "Public sector Work Visa",visaExpiryDate = "2024-07-11", visaNumber = "229357008", passportType = "Normal", moireference = "394046554",visaIssueDate = "2024-03-12")


var sampleUserDataFour = UserData(passportName = "ROSALIE RESTIFICAR DOTILLOS", passportNumber = "P5729628A", expirationName = "2028-01-22", dobValue = "1976-02-22", nationalityName = "FILIPINO", visaType = "Public sector Work Visa",visaExpiryDate = "2024-06-11", visaNumber = "298357003", passportType = "Normal", moireference = "399043750",visaIssueDate = "2024-02-12")




class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        return bitmap?.let {
            val outputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.toByteArray()
        }
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return byteArray?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    }

    @TypeConverter
    fun fromByteArray(byteArray: ByteArray?): String? {
        return byteArray?.let { String(it) }
    }

    @TypeConverter
    fun toByteArray(string: String?): ByteArray? {
        return string?.toByteArray()
    }
}