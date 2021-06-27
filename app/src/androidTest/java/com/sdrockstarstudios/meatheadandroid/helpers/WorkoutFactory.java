package com.sdrockstarstudios.meatheadandroid.helpers;

import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public abstract class WorkoutFactory {
    public static Workout workoutBuilder(String uuid, String name){
        Workout workout = new Workout();
        workout.workoutName = name;
        workout.workoutUUID = uuid;
        workout.startDate = new Date();
        workout.endDate = workout.startDate;
        return workout;
    }

    public static Exercise exerciseBuilder(String uuid, String name, String parentUUID, boolean repsOnly){
        Exercise exercise = new Exercise();
        exercise.exerciseUUID = uuid;
        exercise.exerciseName = name;
        exercise.parentWorkoutUUID = parentUUID;
        exercise.repsOnly = repsOnly;
        return exercise;
    }

    public static Sets setBuilder(int index, String parentUUID, int weight, int reps){
        Sets set = new Sets();
        set.setUUID = UUID.randomUUID().toString();
        set.index = index;
        set.parentExerciseUUID = parentUUID;
        set.weight = weight;
        set.reps = reps;
        return set;
    }
}
