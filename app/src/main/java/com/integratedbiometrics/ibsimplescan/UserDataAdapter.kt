package com.integratedbiometrics.ibsimplescan

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integratedbiometrics.ibsimplescan.utils.UserData
//import kotlinx.android.synthetic.main.item_user_data.view.*
//import com.bumptech.glide.Glide

class UserDataAdapter(private val userDataList: List<UserData>) :
    RecyclerView.Adapter<UserDataAdapter.UserDataViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDataViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_data, parent, false)
        return UserDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserDataViewHolder, position: Int) {
        val userData = userDataList[position]
//        with(holder.itemView) {
//            textViewPassportName.text = "Name: ${userData.passportName}"
//            textViewPassportNumber.text = "Passport Number: ${userData.passportNumber}"
//            textViewNationalityName.text = "Nationality: ${userData.nationalityName}"
//            textViewDobValue.text = "Date of Birth: ${userData.dobValue}"
//            textViewExpirationName.text = "Expiration: ${userData.expirationName}"
//
//            userData.scanByteOne?.let { Glide.with(context).load(it).into(imageViewScanOne) }
//            userData.scanByteTwo?.let { Glide.with(context).load(it).into(imageViewScanTwo) }
//            userData.scanByteThree?.let { Glide.with(context).load(it).into(imageViewScanThree) }
//        }
    }

    override fun getItemCount() = userDataList.size

    class UserDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}