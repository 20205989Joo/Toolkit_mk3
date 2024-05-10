package com.example.toolkit_management_mk3.models

import android.graphics.Color
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Int,
    var mainText: String,
    var subItems: String,
    var color: Int = Color.BLACK
) : Parcelable
