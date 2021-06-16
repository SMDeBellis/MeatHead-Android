package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sdrockstarstudios.meatheadandroid.model.relations.ExerciseAndSets;
import com.sdrockstarstudios.meatheadandroid.model.tables.Exercise;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface ExerciseDoa {

    @Transaction
    @Query("SELECT * FROM exercise")
    public List<ExerciseAndSets> getExerciseAndSets();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Exercise exercise);
}
