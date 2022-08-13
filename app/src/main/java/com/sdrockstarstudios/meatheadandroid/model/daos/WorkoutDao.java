package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.sdrockstarstudios.meatheadandroid.model.FinishWorkout;
import com.sdrockstarstudios.meatheadandroid.model.relations.WorkoutAndExercises;
import com.sdrockstarstudios.meatheadandroid.model.tables.Workout;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE workout.preplanned = 0")
    Single<List<Workout>> getAllWorkouts();

    @Query("SELECT * FROM workout WHERE workout.preplanned = 1")
    Single<List<Workout>> getAllPreplannedWorkouts();

    @Transaction
    @Query("SELECT * FROM workout INNER JOIN exercise ON workout.workoutUUID = exercise.parentWorkoutUUID WHERE exercise.exerciseName = (:exerciseName) AND workout.preplanned = 0")
    Single<List<WorkoutAndExercises>> getAllWorkoutsWithExercise(String exerciseName);

    @Transaction
    @Query("SELECT * FROM workout INNER JOIN exercise ON workout.workoutUUID = exercise.parentWorkoutUUID WHERE exercise.exerciseName = (:exerciseName) AND workout.preplanned = 1")
    Single<List<WorkoutAndExercises>> getAllPreplannedWorkoutsWithExercise(String exerciseName);

    @Transaction
    @Query("SELECT * FROM workout WHERE workoutUUID = (:uuid)")
    Single<WorkoutAndExercises> getWorkout(String uuid);

    @Query("SELECT DISTINCT exerciseName FROM exercise")
    List<String> getAllExercises();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Workout workout);

    @Update(entity = Workout.class)
    Completable finishWorkout(FinishWorkout ended);
}
