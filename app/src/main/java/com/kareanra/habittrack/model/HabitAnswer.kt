package com.kareanra.habittrack.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = Habit::class, parentColumns = ["id"], childColumns = ["habit_id"], onDelete = CASCADE)
    ],
    indices = [
        Index(value = ["habit_id", "yyyymmdd"], unique = true)
    ]
)
data class HabitAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "input_type")
    val inputType: InputType,
    @ColumnInfo(name = "answer")
    val answer: Int,
    @ColumnInfo(name = "notes")
    val notes: String?,
    @ColumnInfo(name = "yyyymmdd", index = true)
    val yyyymmdd: Int,
    @ColumnInfo(name = "habit_id", index = true)
    val habitId: Long
) : Parcelable {

    constructor(source: Parcel): this(
        id = source.readLong(),
        answer = source.readInt(),
        inputType = source.readEnum<InputType>(),
        notes = source.readString(),
        yyyymmdd = source.readInt(),
        habitId = source.readLong()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(answer)
        dest.writeEnum(inputType)
        dest.writeString(notes)
        dest.writeInt(yyyymmdd)
        dest.writeLong(habitId)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<HabitAnswer> = object : Parcelable.Creator<HabitAnswer> {
            override fun createFromParcel(source: Parcel): HabitAnswer = HabitAnswer(source)

            override fun newArray(size: Int): Array<HabitAnswer?> = arrayOfNulls(size)
        }
    }
}
