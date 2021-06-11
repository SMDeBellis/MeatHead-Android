package com.sdrockstarstudios.meatheadandroid.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercises {
    @PrimaryKey
    public int index;

    @ColumnInfo(name = "exerciseName")
    public String exerciseName;

    @ColumnInfo(name = "parentWorkoutUUID")
    public String parentWorkoutUUID;
}
