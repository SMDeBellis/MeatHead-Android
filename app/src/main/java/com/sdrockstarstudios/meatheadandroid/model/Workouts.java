package com.sdrockstarstudios.meatheadandroid.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.sdrockstarstudios.meatheadandroid.model.converters.DateConverter;

import java.util.Date;

@Entity(tableName = "workouts")
public class Workouts {
    @PrimaryKey
    public String workoutUUID;

    @ColumnInfo(name = "startDate")
    @TypeConverters(DateConverter.class)
    public Date startDate;

    @ColumnInfo(name = "endDate")
    public Date endDate;

    @ColumnInfo(name = "workoutName", defaultValue = "")
    public String workoutName;
}
