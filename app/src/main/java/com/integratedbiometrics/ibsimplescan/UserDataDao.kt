package com.integratedbiometrics.ibsimplescan

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.integratedbiometrics.ibsimplescan.utils.UserData

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(userData: UserData)

    @Query("SELECT * FROM user_data WHERE passportNumber = :passportNumber")
    suspend fun getUserDataByPassportNumber(passportNumber: String): UserData?

    @Query("SELECT * FROM user_data")
    suspend fun getAllUserData(): List<UserData>
}