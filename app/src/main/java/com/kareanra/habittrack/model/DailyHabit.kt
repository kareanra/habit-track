package com.kareanra.habittrack.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = Day::class, parentColumns = ["id"], childColumns = ["day_id"], onDelete = CASCADE),
    ForeignKey(entity = Habit::class, parentColumns = ["id"], childColumns = ["habit_id"], onDelete = CASCADE)
])
data class DailyHabit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "completed")
    val completed: Boolean,
    @ColumnInfo(name = "notes")
    val notes: String?,
    @ColumnInfo(name = "day_id", index = true)
    val dayId: Long,
    @ColumnInfo(name = "habit_id", index = true)
    val habitId: Long
) : Parcelable {

    constructor(source: Parcel): this(
        id = source.readLong(),
        completed = source.readInt() == 1,
        notes = source.readString(),
        dayId = source.readLong(),
        habitId = source.readLong()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (completed) 1 else 0)
        dest.writeString(notes)
        dest.writeLong(dayId)
        dest.writeLong(habitId)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DailyHabit> = object : Parcelable.Creator<DailyHabit> {
            override fun createFromParcel(source: Parcel): DailyHabit = DailyHabit(source)

            override fun newArray(size: Int): Array<DailyHabit?> = arrayOfNulls(size)
        }
    }
}