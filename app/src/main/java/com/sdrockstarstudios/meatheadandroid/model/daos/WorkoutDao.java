package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface WorkoutDao {
    @Transaction
    @Query("SELECT * FROM workout WHERE workoutUUID = :uuid")
    Single<WorkoutAndExercises> getWorkout(String uuid);

    @Query("SELECT DISTINCT exerciseName FROM exercise")
    List<String> getAllExercises();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Workout workout);
}
