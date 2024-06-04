package com.integratedbiometrics.ibsimplescan.utils

data class PassportInfo(
    val documentType: String,
    val countryCode: String,
    val surname: String,
    val givenNames: String,
    val passportNumber: String,
    val nationality: String,
    val birthDate: String,
    val sex: String,
    val expirationDate: String,
    val personalNumber: String
)
object MyMrzParser {

    fun parseMRZ(mrz: String): PassportInfo? {
        val lines = mrz.split("\n")
        if (lines.size < 2) return null

        val line1 = lines[0]
        val line2 = lines[1]

        val documentType = line1.substring(0, 1)
        val countryCode = line1.substring(2, 5)
        val nameParts = line1.substring(5).split("<<")
        val surname = nameParts[0].replace("<", " ").trim()
        val givenNames = nameParts[1].replace("<", " ").trim()

        val passportNumber = line2.substring(0, 9).replace("<", "")
        val nationality = line2.substring(10, 13)
        val birthDate = formatMRZDate(line2.substring(13, 19))
        val sex = line2.substring(20, 21)
        val expirationDate = formatMRZDate(line2.substring(21, 27))
        val personalNumber = line2.substring(28, 42).replace("<", "")

        return PassportInfo(
            documentType,
            countryCode,
            surname,
            givenNames,
            passportNumber,
            nationality,
            birthDate,
            sex,
            expirationDate,
            personalNumber
        )
    }

    private fun formatMRZDate(mrzDate: String): String {
        return "19${mrzDate.substring(0, 2)}-${mrzDate.substring(2, 4)}-${mrzDate.substring(4, 6)}"
    }
}