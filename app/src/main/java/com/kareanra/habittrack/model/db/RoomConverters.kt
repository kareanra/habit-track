package com.kareanra.habittrack.model.db

import androidx.room.TypeConverter
import com.kareanra.habittrack.model.InputType

object RoomConverters {
    @TypeConverter
    @JvmStatic
    fun inputTypeToInt(inputType: InputType?) =
        inputType?.ordinal

    @TypeConverter
    @JvmStatic
    fun intToInputType(int: Int?) =
        int?.let {
            InputType::class.java.enumConstants[it]
        }
}
