package com.sdrockstarstudios.meatheadandroid.model.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.sdrockstarstudios.meatheadandroid.model.tables.Sets;

import java.util.List;

@Dao
public interface SetsDao {
    @Query("SELECT * FROM sets")
    List<Sets> getAll();

    @Query("SELECT * FROM sets WHERE parentExerciseUUID IN (:uuid)")
    List<Sets> loadAllByParentExerciseUUID(String uuid);
}
