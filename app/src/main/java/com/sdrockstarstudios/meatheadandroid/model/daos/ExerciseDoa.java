package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface ExerciseDoa {

    @Transaction
    @Query("SELECT * FROM exercise")
    public Single<List<ExerciseAndSets>> getExerciseAndSets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Exercise exercise);

    @Delete
    Completable delete(Exercise exercise);
}
