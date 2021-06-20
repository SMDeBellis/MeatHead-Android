package com.sdrockstarstudios.meatheadandroid.helpers;

import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;
import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.Date;

public abstract class WorkoutFactory {
    public static Workout workoutBuilder(String uuid, String name){
        Workout workout = new Workout();
        workout.workoutName = name;
        workout.workoutUUID = uuid;
        workout.startDate = new Date();
        workout.endDate = workout.startDate;
        return workout;
    }

    public static Exercise exerciseBuilder(String uuid, String name, String parentUUID){
        Exercise exercise = new Exercise();
        exercise.exerciseUUID = uuid;
        exercise.exerciseName = name;
        exercise.parentWorkoutUUID = parentUUID;
        return exercise;
    }

    public static Sets setBuilder(int index, String parentUUID, int weight, int reps, boolean repsOnly){
        Sets set = new Sets();
        set.index = index;
        set.parentExerciseUUID = parentUUID;
        set.weight = weight;
        set.reps = reps;
        set.repsOnly = repsOnly;
        return set;
    }
}
