package com.sdrockstarstudios.meatheadandroid.model.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises",
    foreignKeys = {
        @ForeignKey(
                entity = Workout.class,
                parentColumns = "workoutUUID",
                childColumns = "parentWorkoutUUID",
                onDelete = ForeignKey.CASCADE
        )
    }
)
public class Exercise {
    @PrimaryKey
    public String exerciseUUID;

    @ColumnInfo(name = "exerciseName")
    public String exerciseName;

    @ColumnInfo(name = "parentWorkoutUUID")
    public String parentWorkoutUUID;
}
