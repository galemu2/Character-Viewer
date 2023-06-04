package com.sample.simpsonsviewer.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Icon(
    val Height: String,
    val URL: String,
    val Width: String
):Parcelable