package com.sdrockstarstudios.meatheadandroid.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;

import java.util.List;

public class ExerciseAndSets {
    @Embedded
    public Exercise exercise = null;

    @Relation(
            parentColumn = "exerciseUUID",
            entityColumn = "parentExerciseUUID"
    )
    public List<Sets> setList = null;
}
