package com.sdrockstarstudios.meatheadandroid.model.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "sets",
    indices = {
        @Index(value = {"parentExerciseUUID", "index"}, unique = true)
    },
    foreignKeys = {
        @ForeignKey(
                entity = Exercise.class,
                parentColumns = "exerciseUUID",
                childColumns = "parentExerciseUUID",
                onDelete = ForeignKey.CASCADE
        )
    }
)
public class Sets {

//    public Sets(int index, String parentExerciseUUID, int weight, int reps, boolean repsOnly){
//        this.index = index;
//        this.parentExerciseUUID = parentExerciseUUID;
//        this.weight = weight;
//        this.reps = reps;
//        this.repsOnly = repsOnly;
//    }

    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @NonNull
    @ColumnInfo(name = "index")
    public Integer index;

    @ColumnInfo(name = "parentExerciseUUID")
    public String parentExerciseUUID;

    @ColumnInfo(name = "weight")
    public int weight;

    @ColumnInfo(name = "reps")
    public int reps;

    @ColumnInfo(name = "repsOnly")
    public boolean repsOnly;
}
