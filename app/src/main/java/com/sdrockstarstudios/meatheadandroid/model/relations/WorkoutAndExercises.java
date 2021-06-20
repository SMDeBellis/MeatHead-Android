package com.sdrockstarstudios.meatheadandroid.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.List;

public class WorkoutAndExercises {
    @Embedded
    public Workout workout = null;

    @Relation(
            parentColumn = "workoutUUID",
            entityColumn = "parentWorkoutUUID",
            entity = Exercise.class
    )
    public List<ExerciseAndSets> exercisesAndSets = null;
}
