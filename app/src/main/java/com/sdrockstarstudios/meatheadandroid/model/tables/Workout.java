package com.sdrockstarstudios.meatheadandroid.model.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.sdrockstarstudios.meatheadandroid.model.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "workout")
public class Workout {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "workoutUUID")
    public String workoutUUID;

    @ColumnInfo(name = "startDate")
    @TypeConverters(DateConverter.class)
    public Date startDate;

    @ColumnInfo(name = "endDate")
    @TypeConverters(DateConverter.class)
    public Date endDate;

    @ColumnInfo(name = "workoutName", defaultValue = "")
    public String workoutName;

    @NonNull
    @ColumnInfo(name = "preplanned", defaultValue = "0")
    public Boolean preplanned = false;
}
