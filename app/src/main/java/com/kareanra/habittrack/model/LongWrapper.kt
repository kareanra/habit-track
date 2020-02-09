package com.kareanra.habittrack.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class LongWrapper(
    val value: Long?
) : Parcelable
