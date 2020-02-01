package com.kareanra.habittrack.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Day(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "yyyymmdd", index = true)
    val yyyymmdd: Int
) : Parcelable {

    constructor(source: Parcel): this(
        id = source.readLong(),
        yyyymmdd = source.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(yyyymmdd)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Day> = object : Parcelable.Creator<Day> {
            override fun createFromParcel(source: Parcel): Day = Day(source)

            override fun newArray(size: Int): Array<Day?> = arrayOfNulls(size)
        }
    }
}