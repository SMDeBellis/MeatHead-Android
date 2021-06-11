package com.sdrockstarstudios.meatheadandroid.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sets")
public class Sets {
    @PrimaryKey
    public int index;

    @ColumnInfo(name = "parentExerciseUUID")
    public String parentExerciseUUID;

    @ColumnInfo(name = "weight")
    public int weight;

    @ColumnInfo(name = "reps")
    public int reps;

    @ColumnInfo(name = "repsOnly")
    public boolean repsOnly;
}
