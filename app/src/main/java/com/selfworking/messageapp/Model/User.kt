package com.selfworking.messageapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class user(val uid: String , val username: String , val profileImageUrl: String ) : Parcelable{
    constructor(): this("" ,"" ,"")
}