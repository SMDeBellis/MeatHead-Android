package com.sdrockstarstudios.meatheadandroid.model;

import androidx.room.TypeConverters;

import com.sdrockstarstudios.meatheadandroid.model.converters.DateConverter;

import java.util.Calendar;
import java.util.Date;

public class FinishWorkout {
    public String workoutUUID;

    @TypeConverters(DateConverter.class)
    public Date endDate;

    public FinishWorkout(String uuid){
        workoutUUID = uuid;
        endDate = Calendar.getInstance().getTime();
    }
}
