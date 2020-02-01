package com.kareanra.habittrack.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name="ordinal", index = true)
    val ordinal: Int = -1
) : Parcelable {

    constructor(source: Parcel): this(
        id = source.readLong(),
        name = source.readString()!!,
        ordinal = source.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeInt(ordinal)
    }

    override fun describeContents() = 0

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<Habit> = object : Parcelable.Creator<Habit> {
            override fun createFromParcel(source: Parcel): Habit = Habit(source)

            override fun newArray(size: Int): Array<Habit?> = arrayOfNulls(size)
        }
    }
}