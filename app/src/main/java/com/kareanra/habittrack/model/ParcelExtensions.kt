package com.kareanra.habittrack.model

import android.os.Parcel

fun <E : Enum<E>> Parcel.writeEnum(enum: E) =
    writeInt(enum.ordinal)

inline fun <reified E : Enum<E>> Parcel.readEnum(): E {
    val intValue = readInt()
    val enumConstants = E::class.java.enumConstants
    require(intValue in enumConstants.indices) { "Integer value $intValue not within valid range" }
    return enumConstants[intValue]
}
