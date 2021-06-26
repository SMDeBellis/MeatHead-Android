package com.sdrockstarstudios.meatheadandroid.model;

import java.util.Calendar;
import java.util.Date;

public class FinishWorkout {
    String workoutUUID;
    Date endDate;

    public FinishWorkout(String uuid){
        workoutUUID = uuid;
        endDate = Calendar.getInstance().getTime();
    }
}
