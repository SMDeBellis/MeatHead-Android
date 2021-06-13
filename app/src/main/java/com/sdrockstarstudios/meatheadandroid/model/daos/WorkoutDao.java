package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;

import io.reactivex.Single;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE workoutUUID = :uuid")
    Single<WorkoutAndExercises> getWorkout(String uuid);
}
